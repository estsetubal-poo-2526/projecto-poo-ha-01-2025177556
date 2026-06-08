import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartaTest {

    @Test
    public void revelarEsconderMarcarEncontrado() {
        CartaNormal c = new CartaNormal(5);
        assertEquals(5, c.getIdImagem());
        assertEquals(EstadoCarta.ESCONDIDA, c.getEstado());

        c.revelar();
        assertEquals(EstadoCarta.VISIVEL, c.getEstado());

        c.esconder();
        assertEquals(EstadoCarta.ESCONDIDA, c.getEstado());

        c.revelar();
        c.marcarComoParEncontrado();
        assertEquals(EstadoCarta.ENCONTRADA, c.getEstado());
    }
}
