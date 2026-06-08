import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TabuleiroTest {

    @Test
    public void inicializarModoNumerosDimensionsAndNullImages() {
        Tabuleiro t = new Tabuleiro(Modo.NUMEROS);
        assertEquals(4, t.getTotalLinhas());
        assertEquals(5, t.getTotalColunas());

        for (int r = 0; r < t.getTotalLinhas(); r++) {
            for (int c = 0; c < t.getTotalColunas(); c++) {
                assertNotNull(t.getCarta(r, c));
                assertNull(t.getImagemPath(r, c));
            }
        }
    }

    @Test
    public void todosParesEncontradosFalseInitially() {
        Tabuleiro t = new Tabuleiro(Modo.NUMEROS);
        assertFalse(t.todosParesEncontrados());
    }
}
