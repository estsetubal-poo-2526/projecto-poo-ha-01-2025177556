import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.net.URL;

public class Tabuleiro {
    private List<Carta> cartas;
    private List<String> imagemPaths; 
    private final int TOTAL_LINHAS = 4;
    private final int TOTAL_COLUNAS = 5;

    public Tabuleiro(Modo modo){
        cartas = new ArrayList<>();
        imagemPaths = new ArrayList<>();
        inicializarCartas(modo);
    }

    private List<String> listarImagensDisponiveis(){
        List<String> imgs = new ArrayList<>();
        try{
            URL url = getClass().getResource("/images");
            if (url != null && "file".equals(url.getProtocol())){
                File dir = new File(url.toURI());
                File[] files = dir.listFiles();
                if (files != null){
                    for (File f : files){
                        if (f.isFile() && f.getName().toLowerCase().endsWith(".png")){
                            imgs.add(removeExt(f.getName()));
                        }
                    }
                }
            }

            // also try the classpath root (resources placed directly in src/main/resources)
            if (imgs.isEmpty()){
                URL rootUrl = getClass().getResource("/");
                if (rootUrl != null && "file".equals(rootUrl.getProtocol())){
                    File rootDir = new File(rootUrl.toURI());
                    File[] rootFiles = rootDir.listFiles();
                    if (rootFiles != null){
                        for (File f : rootFiles){
                            if (f.isFile() && f.getName().toLowerCase().endsWith(".png")){
                                imgs.add(removeExt(f.getName()));
                            }
                        }
                    }
                }
            }
        } catch(Exception e){
            
        }

        
        if (imgs.isEmpty()){
            File dir = new File("src/main/resources");
            if (!dir.exists()) dir = new File("src/images");
            if (!dir.exists()) dir = new File("images");
            if (dir.exists()){
                File[] files = dir.listFiles();
                if (files != null){
                    for (File f : files){
                        if (f.isFile() && f.getName().toLowerCase().endsWith(".png")){
                            imgs.add(removeExt(f.getName()));
                        }
                    }
                }
            }
        }
        return imgs;
    }

    private String removeExt(String name){
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(0,i) : name;
    }

    private void inicializarCartas(Modo modo) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= 10; i++) ids.add(i);

        if (modo == Modo.NUMEROS) {
            
            for (Integer id : ids) {
                cartas.add(new CartaNormal(id)); imagemPaths.add(null);
                cartas.add(new CartaNormal(id)); imagemPaths.add(null);
            }
            
            int replaced = 0;
            for (int i = 0; i < cartas.size() && replaced < 2; i++) {
                if (cartas.get(i).getIdImagem() == 1) {
                    cartas.set(i, new CartaBonus(1));
                    replaced++;
                }
            }
            
            embaralharCartasComImagens();
            return;
        }

        
        List<String> available = listarImagensDisponiveis();
        System.out.println("Available images: " + available);
        int pairsNeeded = 9; 

        
        
        String jokerName = null;
        
        for (String a : available) {
            String low = a.toLowerCase();
            if (low.equals("ace") || low.equals("as") || low.equals("ás")) { jokerName = a; break; }
        }
        
        if (jokerName == null) {
            for (String a : available) {
                String low = a.toLowerCase();
                if (low.endsWith("_1")) { jokerName = a; break; }
            }
        }
        
        if (jokerName == null) {
            for (String a : available) {
                String low = a.toLowerCase();
                if (low.contains("ace")) { jokerName = a; break; }
            }
        }
        
        if (jokerName == null) {
            for (String a : available) {
                String low = a.toLowerCase();
                if (low.contains("as") || low.contains("ás")) { jokerName = a; break; }
            }
        }
        if (jokerName == null && available.contains("joker")) jokerName = "joker";
        if (jokerName == null && !available.isEmpty()) jokerName = available.get(0);

        System.out.println("Chosen jokerName: " + jokerName);

        
        List<String> pool = new ArrayList<>(available);
        if (jokerName != null) pool.remove(jokerName);

        List<String> chosen = new ArrayList<>();
        if (!pool.isEmpty()){
            Collections.shuffle(pool, new Random());
            for (int i = 0; i < pairsNeeded; i++){
                chosen.add(pool.get(i % pool.size()));
            }
        } else {
            
            String[] suits = {"hearts","diamonds","clubs","spades"};
            List<String> deck = new ArrayList<>();
            for (String s : suits) {
                for (int r = 1; r <= 13; r++) deck.add(s + "_" + r);
            }
            Collections.shuffle(deck, new Random());
            chosen = deck.subList(0, pairsNeeded);
        }

        int idCounter = 1;
        for (String name : chosen) {
            cartas.add(new CartaNormal(idCounter)); imagemPaths.add(name);
            cartas.add(new CartaNormal(idCounter)); imagemPaths.add(name);
            idCounter++;
        }

        
        if (jokerName == null) jokerName = "joker";
        cartas.add(new CartaBonus(idCounter)); imagemPaths.add(jokerName);
        cartas.add(new CartaBonus(idCounter)); imagemPaths.add(jokerName);

        embaralharCartasComImagens();
    }

    private void embaralharCartasComImagens(){
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cartas.size(); i++) indices.add(i);
        Collections.shuffle(indices);
        List<Carta> newCartas = new ArrayList<>();
        List<String> newImgs = new ArrayList<>();
        for (int idx : indices){
            newCartas.add(cartas.get(idx));
            newImgs.add(imagemPaths.get(idx));
        }
        cartas = newCartas;
        imagemPaths = newImgs;
    }

    public Carta getCarta(int linha, int coluna){
        int index = (linha * TOTAL_COLUNAS) + coluna;
        return cartas.get(index);
    }

    public String getImagemPath(int linha, int coluna){
        int index = (linha * TOTAL_COLUNAS) + coluna;
        return imagemPaths.get(index);
    }

    public int getTotalLinhas(){
        return TOTAL_LINHAS;
    }

    public int getTotalColunas(){
        return TOTAL_COLUNAS;
    }

    public boolean todosParesEncontrados() {
        for (Carta c : cartas) {
            if (c.getEstado() != EstadoCarta.ENCONTRADA){
                return false;
            }
        }
        return true;
    }
}
