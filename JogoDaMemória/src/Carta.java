public abstract class Carta {
    protected int idImagem;
    protected EstadoCarta estado;

    public Carta(int idImagem) {
        this.idImagem = idImagem;
        estado = EstadoCarta.ESCONDIDA;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public EstadoCarta getEstado() {
        return estado;
    }

    public void revelar() {
        if (estado == EstadoCarta.ESCONDIDA) {
            estado = EstadoCarta.VISIVEL;
        }
    }

    public void esconder() {
        if (estado == EstadoCarta.VISIVEL) {
            estado = EstadoCarta.ESCONDIDA;
        }
    }

    public void marcarComoParEncontrado() {
        estado = EstadoCarta.ENCONTRADA;
    }
}
