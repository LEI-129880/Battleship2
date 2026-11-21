package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes adicionais de Carrack (129890), criados na ficha 4 para maximizar a cobertura.
 * Os testes originais continuam em CarrackTest.
 *
 * @author Rafael Balão (129890)
 */
@DisplayName("Testes adicionais da classe Carrack")
public class AdditionalCarrackTest {

    private Carrack carrack;

    @BeforeEach
    void setUp() {
        carrack = new Carrack(Compass.NORTH, new Position(5, 5));
    }

    @Test
    @DisplayName("Os limites da nau horizontal correspondem às posições ocupadas")
    void testBoundsHorizontal() {
        Carrack carrackEast = new Carrack(Compass.EAST, new Position(2, 3));
        assertEquals(2, carrackEast.getTopMostPos(), "Error: The topmost position should be 2.");
        assertEquals(2, carrackEast.getBottomMostPos(), "Error: The bottommost position should be 2.");
        assertEquals(3, carrackEast.getLeftMostPos(), "Error: The leftmost position should be 3.");
        assertEquals(5, carrackEast.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    @Test
    @DisplayName("occupies: a nau ocupa as 3 posições e não as vizinhas")
    void testOccupies() {
        assertTrue(carrack.occupies(new Position(5, 5)), "Error: Carrack should occupy (5, 5).");
        assertTrue(carrack.occupies(new Position(6, 5)), "Error: Carrack should occupy (6, 5).");
        assertTrue(carrack.occupies(new Position(7, 5)), "Error: Carrack should occupy (7, 5).");
        assertFalse(carrack.occupies(new Position(8, 5)), "Error: Carrack should not occupy (8, 5).");
        assertFalse(carrack.occupies(new Position(5, 6)), "Error: Carrack should not occupy (5, 6).");
    }

    @Test
    @DisplayName("shoot atinge só a posição alvo da nau")
    void testShoot() {
        carrack.shoot(new Position(6, 5));
        assertFalse(carrack.getPositions().get(0).isHit(), "Error: The first position should not be hit.");
        assertTrue(carrack.getPositions().get(1).isHit(), "Error: The second position should be hit.");
        assertFalse(carrack.getPositions().get(2).isHit(), "Error: The third position should not be hit.");
    }

    @Test
    @DisplayName("tooCloseTo: navio encostado à nau está demasiado perto")
    void testTooCloseTo() {
        assertTrue(carrack.tooCloseTo(new Barge(Compass.NORTH, new Position(8, 6))), "Error: A barge touching the carrack's corner should be too close.");
        assertFalse(carrack.tooCloseTo(new Barge(Compass.NORTH, new Position(9, 5))), "Error: A barge two rows below should not be too close.");
    }

    @Test
    @DisplayName("isInsideBoard: nau que sai pelo fundo do tabuleiro está fora")
    void testIsInsideBoard() {
        assertTrue(carrack.isInsideBoard(), "Error: Carrack at (5,5) should be inside the board.");
        assertFalse(new Carrack(Compass.SOUTH, new Position(Game.BOARD_SIZE - 2, 0)).isInsideBoard(),
                "Error: Carrack crossing the bottom edge should not be inside the board.");
    }
}
