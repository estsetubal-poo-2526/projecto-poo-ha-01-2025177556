public class CartaBonus extends Carta implements Efeito {
    public CartaBonus(int idImagem){
        super(idImagem);
    }

    @Override
    public void aplicarEfeito(JogoMemoria jogo) {
        jogo.adicionarTentativas(2);
        System.out.println("Bónus! Ganhaste 2 tentativas.");
    }
}
