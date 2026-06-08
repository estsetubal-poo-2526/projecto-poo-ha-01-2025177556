public class JogoMemoria {
    private Tabuleiro tabuleiro;
    private int tentativasRestantes;
    private Carta primeiraCartaVirada;
    private Carta segundaCartaVirada;
    private EstadoJogo estadoAtual;
    private boolean ultimaJogadaFoiMatch;
    private Modo modo;

    public JogoMemoria(){
        iniciarNovoJogo(Modo.NUMEROS);
    }

    public void iniciarNovoJogo(){
        iniciarNovoJogo(Modo.NUMEROS);
    }

    public void iniciarNovoJogo(Modo modo){
        this.modo = modo;
        this.tabuleiro = new Tabuleiro(modo);
        this.tentativasRestantes = 20;
        this.primeiraCartaVirada = null;
        this.segundaCartaVirada = null;
        this.estadoAtual = EstadoJogo.EM_ANDAMENTO;
        this.ultimaJogadaFoiMatch = false;
    }

    public void fazerJogada(int linha, int coluna) {
        if (estadoAtual != EstadoJogo.EM_ANDAMENTO) {
            System.out.println("O jogo terminou!");
            return;
        }

        Carta cartaSelecionada = tabuleiro.getCarta(linha, coluna);

        if (cartaSelecionada.getEstado() != EstadoCarta.ESCONDIDA) {
            return;
        }

        cartaSelecionada.revelar();

        if (primeiraCartaVirada == null) {
            primeiraCartaVirada = cartaSelecionada;
            ultimaJogadaFoiMatch = false;
        } else if (segundaCartaVirada == null) {
            segundaCartaVirada = cartaSelecionada;
            tentativasRestantes--;

            if (primeiraCartaVirada.getIdImagem() == segundaCartaVirada.getIdImagem()) {
                primeiraCartaVirada.marcarComoParEncontrado();
                segundaCartaVirada.marcarComoParEncontrado();
                if (primeiraCartaVirada instanceof Efeito) {
                    ((Efeito) primeiraCartaVirada).aplicarEfeito(this);
                } else if (segundaCartaVirada instanceof Efeito) {
                    ((Efeito) segundaCartaVirada).aplicarEfeito(this);
                }
                ultimaJogadaFoiMatch = true;
                primeiraCartaVirada = null;
                segundaCartaVirada = null;
                verificarEstadoJogo();
            } else {
                ultimaJogadaFoiMatch = false;
            }
        }
    }

    public void esconderParesSelecionadosIfMismatch(){
        if (primeiraCartaVirada != null && segundaCartaVirada != null) {
            primeiraCartaVirada.esconder();
            segundaCartaVirada.esconder();
        }
        primeiraCartaVirada = null;
        segundaCartaVirada = null;
        verificarEstadoJogo();
    }

    private void verificarEstadoJogo() {
        if (tabuleiro.todosParesEncontrados()) {
            estadoAtual = EstadoJogo.VITORIA;
            System.out.println("Vitória! Encontraste todos os pares.");
        } else if (tentativasRestantes <= 0) {
            estadoAtual = EstadoJogo.DERROTA;
            System.out.println("Derrota! Ficaste sem tentativas.");
        }
    }

    public int getTentativasRestantes() {
        return tentativasRestantes;
    }
    public EstadoJogo getEstadoAtual() {
        return estadoAtual;
    }

    public void adicionarTentativas(int i){
        tentativasRestantes = tentativasRestantes + i;
    }

    public Tabuleiro getTabuleiro(){
        return tabuleiro;
    }

    public Modo getModo(){
        return modo;
    }

    public boolean ultimaJogadaFoiMatch(){
        return ultimaJogadaFoiMatch;
    }
}