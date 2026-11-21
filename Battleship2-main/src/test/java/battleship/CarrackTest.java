package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// Imports any necessary classes like Position, Compass, etc.

import java.util.List;

/**
 * Test class for the Carrack class.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 5
 * - getSize: 1
 * - stillFloating: 2
 * - getPositions: 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
@DisplayName("Testes da classe Carrack")
public class CarrackTest {

    private Carrack carrack;

    @BeforeEach
    void setUp() {
        carrack = new Carrack(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        carrack = null;
    }

    /**
     * Test for the constructor with valid input.
     * Cyclomatic Complexity: 5
     */
    @Test
    @DisplayName("O construtor cria uma nau com categoria, orientação e 3 posições")
    void testConstructor() {
        assertNotNull(carrack, "Error: Carrack instance should not be null.");
        assertEquals("Nau", carrack.getCategory(), "Error: Carrack's category should be 'Nau'.");
        assertEquals(Compass.NORTH, carrack.getBearing(), "Error: Carrack's bearing is incorrect.");
        assertEquals(3, carrack.getSize(), "Error: Carrack's size should be 3.");
        assertEquals(3, carrack.getPositions().size(), "Error: Carrack should have 3 positions.");
    }

    /**
     * Test for the getSize method (basic validation).
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("getSize devolve 3")
    void testGetSize() {
        assertEquals(3, carrack.getSize(), "Error: The size of the Carrack should be 3.");
    }

    /**
     * Test for the getPositions method (all positions must be correct).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("Virada a NORTE ocupa a origem e as 2 células abaixo")
    void testGetPositions() {
        List<IPosition> positions = carrack.getPositions();

        // Validate the number of positions
        assertEquals(3, positions.size(), "Error: The Carrack should have three positions.");

        // Validate each position
        assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect.");
        assertEquals(new Position(6, 5), positions.get(1), "Error: The second position is incorrect.");
        assertEquals(new Position(7, 5), positions.get(2), "Error: The third position is incorrect.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getTopMostPos devolve a linha de cima")
    void testGetTopMostPos() {
        assertEquals(5, carrack.getTopMostPos(), "Error: The topmost position of the Carrack should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getBottomMostPos devolve a linha de baixo")
    void testGetBottomMostPos() {
        assertEquals(7, carrack.getBottomMostPos(), "Error: The bottommost position of the Carrack should be 7.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getLeftMostPos devolve a coluna da esquerda")
    void testGetLeftMostPos() {
        assertEquals(5, carrack.getLeftMostPos(), "Error: The leftmost position of the Carrack should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getRightMostPos devolve a coluna da direita")
    void testGetRightMostPos() {
        assertEquals(5, carrack.getRightMostPos(), "Error: The rightmost position of the Carrack should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("stillFloating: a nau intacta flutua")
    void testStillFloating1() {
        assertTrue(carrack.stillFloating(), "Error: Carrack should initially be floating.");
    }

    /**
     * Test for the stillFloating method (some positions hit).
     */
    @Test
    @DisplayName("stillFloating: a nau com 1 posição atingida ainda flutua")
    void testStillFloating2() {
        // Hit one position
        carrack.getPositions().get(0).shoot();
        assertTrue(carrack.stillFloating(), "Error: Carrack should still be floating when only one position is hit.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    @DisplayName("stillFloating: a nau com todas as posições atingidas afunda")
    void testStillFloating3() {
        carrack.getPositions().forEach(position -> position.shoot());
        assertFalse(carrack.stillFloating(), "Error: Carrack should not be floating when all positions are hit.");
    }

    /**
     * Test for the constructor with null inputs.
     * Expect exception.
     */
    @Test
    @DisplayName("O construtor lança NullPointerException com argumentos nulos")
    void testConstructorWithNull() {
        assertThrows(NullPointerException.class, () -> new Carrack(null, null),
                "Error: Expected NullPointerException for null input.");
    }

    /**
     * Test for positions with EAST direction.
     */
    @Test
    @DisplayName("Virada a ESTE ocupa a origem e as 2 células à direita")
    void testConstructorDirectionEast() {
        Carrack carrackEast = new Carrack(Compass.EAST, new Position(5, 5));
        List<IPosition> positions = carrackEast.getPositions();

        assertNotNull(carrackEast, "Error: Carrack with EAST direction should not be null.");
        assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for EAST direction.");
        assertEquals(new Position(5, 6), positions.get(1), "Error: The second position is incorrect for EAST direction.");
        assertEquals(new Position(5, 7), positions.get(2), "Error: The third position is incorrect for EAST direction.");
    }

    /**
     * Test for positions with SOUTH direction.
     */
    @Test
    @DisplayName("Virada a SUL ocupa a origem e as 2 células abaixo")
    void testConstructorDirectionSouth() {
        Carrack carrackSouth = new Carrack(Compass.SOUTH, new Position(5, 5));
        List<IPosition> positions = carrackSouth.getPositions();

        assertNotNull(carrackSouth, "Error: Carrack with SOUTH direction should not be null.");
        assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for SOUTH direction.");
        assertEquals(new Position(6, 5), positions.get(1), "Error: The second position is incorrect for SOUTH direction.");
        assertEquals(new Position(7, 5), positions.get(2), "Error: The third position is incorrect for SOUTH direction.");
    }

    /**
     * Test for positions with WEST direction.
     */
    @Test
    @DisplayName("Virada a OESTE ocupa a origem e as 2 células à direita")
    void testConstructorDirectionWest() {
        Carrack carrackWest = new Carrack(Compass.WEST, new Position(5, 5));
        List<IPosition> positions = carrackWest.getPositions();

        assertNotNull(carrackWest, "Error: Carrack with WEST direction should not be null.");
        assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for WEST direction.");
        assertEquals(new Position(5, 6), positions.get(1), "Error: The second position is incorrect for WEST direction.");
        assertEquals(new Position(5, 7), positions.get(2), "Error: The third position is incorrect for WEST direction.");
    }

    /**
     * Test for the constructor's exception with invalid parameters.
     * Ensures assertions in the constructor work properly.
     */
    @Test
    @DisplayName("O construtor lança NullPointerException com orientação ou posição nula")
    void testConstructorWithInvalidInput() {
        assertThrows(NullPointerException.class, () -> new Carrack(null, new Position(5, 5)),
                "Error: Should throw NullPointerException for null bearing.");
        assertThrows(NullPointerException.class, () -> new Carrack(Compass.NORTH, null),
                "Error: Should throw NullPointerException for null position.");
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
