import javafx.application.Application;
import javafx.application.Platform;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.transform.Rotate;

public class App extends Application {
    private JogoMemoria jogo;
    private boolean animado = false;
    private Label tentativasLabel;
    private Stage primaryStage;
    private StackPane[][] panes; 
    private int selR1 = -1, selC1 = -1, selR2 = -1, selC2 = -1;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        jogo = new JogoMemoria();
        Scene menu = criarMenuScene();
        stage.setTitle("Jogo da Memória");
        stage.setScene(menu);
        stage.show();
    }

    private Scene criarMenuScene(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("menu-root");

        Label titulo = new Label("Jogo da Memória");
        titulo.setFont(Font.font(32));
        titulo.getStyleClass().add("title");

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbNums = new RadioButton("Números");
        rbNums.setToggleGroup(tg);
        rbNums.setSelected(true);
        rbNums.getStyleClass().addAll("mode-radio");
        RadioButton rbCards = new RadioButton("Cartas");
        rbCards.setToggleGroup(tg);
        rbCards.getStyleClass().addAll("mode-radio");

        Button start = new Button("Start");
        start.getStyleClass().add("start-button");
        start.setOnAction(e -> {
            Modo modo = rbCards.isSelected() ? Modo.CARTAS : Modo.NUMEROS;
            jogo.iniciarNovoJogo(modo);
            primaryStage.setScene(criarJogoScene());
        });

        Button exit = new Button("Sair");
        exit.getStyleClass().add("start-button");
        exit.setOnAction(e -> primaryStage.close());

        VBox controls = new VBox(10, rbNums, rbCards, start, exit);
        controls.setAlignment(Pos.CENTER);
        controls.getStyleClass().add("menu-controls");

        root.getChildren().addAll(titulo, controls);
        Scene scene = new Scene(root, 800, 600);
        try{ scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); }catch(Exception ex){  }
        return scene;
    }

    private Scene criarJogoScene(){
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");
        if (jogo.getModo() == Modo.CARTAS) root.getStyleClass().add("mode-cartas"); else root.getStyleClass().add("mode-numeros");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("game-grid");

        int linhas = jogo.getTabuleiro().getTotalLinhas();
        int colunas = jogo.getTabuleiro().getTotalColunas();

        panes = new StackPane[linhas][colunas];

        for (int r = 0; r < linhas; r++){
            for (int c = 0; c < colunas; c++){
                Carta carta = jogo.getTabuleiro().getCarta(r, c);
                StackPane cardPane = criarCardPane(carta, r, c);
                panes[r][c] = cardPane;
                grid.add(cardPane, c, r);
            }
        }

        tentativasLabel = new Label("Tentativas: " + jogo.getTentativasRestantes());
        tentativasLabel.setFont(Font.font(18));
        tentativasLabel.getStyleClass().add("tentativas-label");

        Button voltar = new Button("Menu");
        voltar.getStyleClass().add("menu-button");
        voltar.setOnAction(e -> primaryStage.setScene(criarMenuScene()));

        VBox topBox = new VBox(10, tentativasLabel, voltar);
        topBox.setPadding(new Insets(10));
        topBox.getStyleClass().add("top-box");

        root.setTop(topBox);
        root.setCenter(grid);

        Scene scene = new Scene(root, 900, 700);
        try{ scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); }catch(Exception ex){  }

        
        final VBox fTopBox = topBox;
        final Scene sceneRef = scene;
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                double topH = fTopBox.getHeight() + 10; 
                double available = sceneRef.getHeight() - topH - 40; 
                int rows = jogo.getTabuleiro().getTotalLinhas();
                double vGap = 10;
                double desiredH = (available - (rows - 1) * vGap) / rows;
                desiredH = Math.max(80, Math.min(180, desiredH));
                double desiredW = desiredH * (120.0 / 150.0);

                if (panes != null) {
                    for (int r = 0; r < panes.length; r++){
                        for (int c = 0; c < panes[0].length; c++){
                            StackPane p = panes[r][c];
                            if (p == null) continue;
                            
                            if (p.getChildren().size() < 2) continue;
                            StackPane frontStack = (StackPane) p.getChildren().get(1);
                            if (frontStack.getChildren().size() > 0 && frontStack.getChildren().get(0) instanceof Rectangle){
                                Rectangle frontRect = (Rectangle) frontStack.getChildren().get(0);
                                frontRect.setWidth(desiredW);
                                frontRect.setHeight(desiredH);
                            }
                            if (frontStack.getChildren().size() > 1 && frontStack.getChildren().get(1) instanceof ImageView){
                                ImageView iv = (ImageView) frontStack.getChildren().get(1);
                                iv.setFitWidth(Math.max(40, desiredW - 20));
                                iv.setFitHeight(Math.max(50, desiredH - 25));
                            }
                            
                            StackPane backStack = (StackPane) p.getChildren().get(0);
                            if (backStack.getChildren().size() > 0 && backStack.getChildren().get(0) instanceof Rectangle){
                                Rectangle backRect = (Rectangle) backStack.getChildren().get(0);
                                backRect.setWidth(desiredW);
                                backRect.setHeight(desiredH);
                            }
                        }
                    }
                }
            });
        });

        
        Platform.runLater(() -> {
            sceneRef.getHeight();
        });

        return scene;
    }

    private StackPane criarCardPane(Carta carta, int linha, int coluna){
        Rectangle front = new Rectangle(120, 150);
        front.setArcWidth(10);
        front.setArcHeight(10);
        front.getStyleClass().add("front-rect");

        Label frontLabel = new Label(String.valueOf(carta.getIdImagem()));
        frontLabel.setFont(Font.font(24));

        
        boolean isBonus = carta instanceof CartaBonus;
        if (isBonus && jogo.getModo() == Modo.NUMEROS){
            front.setFill(Color.GOLD);
            frontLabel.setText("Bónus");
        }

        
        String imgPath = jogo.getTabuleiro().getImagemPath(linha, coluna);
        ImageView iv = null;
        if (imgPath != null) {
            try (java.io.InputStream is = getClass().getResourceAsStream("/images/" + imgPath + ".png")) {
                if (is != null) {
                    Image img = new Image(is);
                    iv = new ImageView(img);
                    iv.setPreserveRatio(true);
                    iv.setFitWidth(100);
                    iv.setFitHeight(125);
                }
            } catch (Exception ex) {
                iv = null;
            }
        }
        final StackPane frontStack = (iv != null) ? new StackPane(front, iv) : new StackPane(front, frontLabel);
        frontStack.getStyleClass().add("card-front");

        final Rectangle costas = new Rectangle(120, 150);
        costas.setFill(Color.DARKGRAY);
        costas.setArcWidth(10);
        costas.setArcHeight(10);
        costas.getStyleClass().add("back-rect");
        Label costasLabel = new Label("? ");
        costasLabel.setTextFill(Color.WHITE);
        costasLabel.setFont(Font.font(28));
        final StackPane backStack = new StackPane(costas, costasLabel);
        backStack.getStyleClass().add("card-back");

        StackPane cardPane = new StackPane();
        cardPane.getChildren().addAll(backStack, frontStack);
        cardPane.getStyleClass().add("card");
        frontStack.setVisible(false);

        cardPane.setOnMouseClicked(e -> {
            if (animado) return;
            if (carta.getEstado() != EstadoCarta.ESCONDIDA) return;

            animado = true;
            flipToFront(cardPane, backStack, frontStack, () -> {
                if (selR1 == -1) { selR1 = linha; selC1 = coluna; }
                else if (selR2 == -1) { selR2 = linha; selC2 = coluna; }

                jogo.fazerJogada(linha, coluna);
                atualizarTentativas();

                if (jogo.ultimaJogadaFoiMatch()){
                    selR1 = selC1 = selR2 = selC2 = -1;
                    animado = false;
                    if (jogo.getEstadoAtual() == EstadoJogo.VITORIA){
                        mostrarVitoria();
                    } else if (jogo.getEstadoAtual() == EstadoJogo.DERROTA) {
                        mostrarDerrota();
                    }
                } else {
                    if (selR2 != -1){
                        PauseTransition pause = new PauseTransition(Duration.millis(800));
                        pause.setOnFinished(ev -> {
                            StackPane p1 = panes[selR1][selC1];
                            StackPane p2 = panes[selR2][selC2];

                            StackPane front1 = (StackPane)p1.getChildren().get(1);
                            StackPane back1 = (StackPane)p1.getChildren().get(0);
                            StackPane front2 = (StackPane)p2.getChildren().get(1);
                            StackPane back2 = (StackPane)p2.getChildren().get(0);

                            flipToBack(p1, front1, back1, () -> {});
                            flipToBack(p2, front2, back2, () -> {});

                            jogo.esconderParesSelecionadosIfMismatch();
                            atualizarTentativas();

                            selR1 = selC1 = selR2 = selC2 = -1;
                            animado = false;

                            if (jogo.getEstadoAtual() == EstadoJogo.DERROTA){ mostrarDerrota(); }
                        });
                        pause.play();
                    } else {
                        animado = false;
                    }
                }
            });
        });

        return cardPane;
    }

    private void atualizarTentativas(){
        tentativasLabel.setText("Tentativas: " + jogo.getTentativasRestantes());
    }



    private void flipToFront(StackPane cardPane, StackPane back, StackPane front, Runnable onFinished){
        RotateTransition rt1 = new RotateTransition(Duration.millis(150), cardPane);
        rt1.setAxis(Rotate.Y_AXIS);
        rt1.setFromAngle(0);
        rt1.setToAngle(90);

        RotateTransition rt2 = new RotateTransition(Duration.millis(150), cardPane);
        rt2.setAxis(Rotate.Y_AXIS);
        rt2.setFromAngle(-90);
        rt2.setToAngle(0);

        rt1.setOnFinished(e -> { back.setVisible(false); front.setVisible(true); });
        SequentialTransition seq = new SequentialTransition(rt1, rt2);
        seq.setOnFinished(e -> onFinished.run());
        seq.play();
    }

    private void flipToBack(StackPane cardPane, StackPane front, StackPane back, Runnable onFinished){
        RotateTransition rt1 = new RotateTransition(Duration.millis(150), cardPane);
        rt1.setAxis(Rotate.Y_AXIS);
        rt1.setFromAngle(0);
        rt1.setToAngle(90);

        RotateTransition rt2 = new RotateTransition(Duration.millis(150), cardPane);
        rt2.setAxis(Rotate.Y_AXIS);
        rt2.setFromAngle(-90);
        rt2.setToAngle(0);

        rt1.setOnFinished(e -> { front.setVisible(false); back.setVisible(true); });
        SequentialTransition seq = new SequentialTransition(rt1, rt2);
        seq.setOnFinished(e -> onFinished.run());
        seq.play();
    }

    private void mostrarVitoria(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Parabéns");
        a.setHeaderText(null);
        a.setContentText("Parabéns! Encontraste todos os pares.");
        javafx.scene.control.DialogPane dp = a.getDialogPane();
        try { dp.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch(Exception ex) { }
        dp.getStyleClass().add("result-dialog");
        a.setOnHidden(ev -> primaryStage.setScene(criarMenuScene()));
        a.show();
    }

    private void mostrarDerrota(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Fim de Jogo");
        a.setHeaderText(null);
        a.setContentText("Ficaste sem tentativas.");
        javafx.scene.control.DialogPane dp = a.getDialogPane();
        try { dp.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch(Exception ex) { }
        dp.getStyleClass().add("result-dialog");
        a.setOnHidden(ev -> primaryStage.setScene(criarMenuScene()));
        a.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}