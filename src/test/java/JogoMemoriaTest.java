import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JogoMemoriaTest {

    @Test
    public void fazerJogadaMatchDeveMarcarParesEncontradosETentarivas() {
        JogoMemoria jogo = new JogoMemoria();
        jogo.iniciarNovoJogo(Modo.NUMEROS);
        Tabuleiro t = jogo.getTabuleiro();
        int linhas = t.getTotalLinhas();
        int colunas = t.getTotalColunas();

        boolean found = false;
        for (int r1 = 0; r1 < linhas && !found; r1++) {
            for (int c1 = 0; c1 < colunas && !found; c1++) {
                for (int r2 = r1; r2 < linhas && !found; r2++) {
                    for (int c2 = (r2 == r1 ? c1 + 1 : 0); c2 < colunas && !found; c2++) {
                        Carta a = t.getCarta(r1, c1);
                        Carta b = t.getCarta(r2, c2);
                        if (a.getIdImagem() == b.getIdImagem() && a.getIdImagem() != 1) {
                            int initial = jogo.getTentativasRestantes();
                            jogo.fazerJogada(r1, c1);
                            jogo.fazerJogada(r2, c2);
                            assertTrue(jogo.ultimaJogadaFoiMatch());
                            assertEquals(EstadoCarta.ENCONTRADA, a.getEstado());
                            assertEquals(EstadoCarta.ENCONTRADA, b.getEstado());
                            assertEquals(initial - 1, jogo.getTentativasRestantes());
                            found = true;
                        }
                    }
                }
            }
        }
        assertTrue(found, "Nenhum par testável encontrado (id != 1).");
    }
}
