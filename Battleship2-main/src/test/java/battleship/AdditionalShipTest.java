package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Testes adicionais de Ship (129890), criados na ficha 4 para maximizar a cobertura.
 * Os testes originais continuam em ShipTest.
 *
 * @author Rafael Balão (129890)
 */
@DisplayName("Testes adicionais da classe abstrata Ship")
public class AdditionalShipTest {

    private Ship ship;

    @BeforeEach
    void setUp() {
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    /**
     * Creates a ship whose positions are given explicitly, so that every
     * branch of the getXxxMostPos loops can be exercised.
     */
    private static Ship customShip(IPosition... cells) {
        Ship custom = new Ship("Teste", Compass.NORTH, cells[0], cells.length) { };
        for (IPosition cell : cells)
            custom.getPositions().add(cell);
        return custom;
    }

    @Test
    @DisplayName("O construtor rejeita categoria, orientação ou posição nulas")
    void testConstructorNulls() {
        Position origin = new Position(1, 1);
        Exception e1 = assertThrows(NullPointerException.class, () -> new Ship(null, Compass.NORTH, origin, 1) { });
        assertEquals("Ship's category must not be null", e1.getMessage(), "Error: Wrong message for null category.");
        Exception e2 = assertThrows(NullPointerException.class, () -> new Ship("Teste", null, origin, 1) { });
        assertEquals("Ship's bearing must not be null", e2.getMessage(), "Error: Wrong message for null bearing.");
        Exception e3 = assertThrows(NullPointerException.class, () -> new Ship("Teste", Compass.NORTH, null, 1) { });
        assertEquals("Ship's position must not be null", e3.getMessage(), "Error: Wrong message for null position.");
    }

    @Test
    @DisplayName("getPosition devolve a posição de origem")
    void testGetPosition() {
        assertEquals(new Position(5, 5), ship.getPosition(), "Error: The origin should be (5, 5).");
    }

    @Test
    @DisplayName("Os limites são calculados em todas as posições, qualquer que seja a ordem")
    void testBoundsUnorderedPositions() {
        Ship custom = customShip(new Position(5, 5), new Position(3, 2), new Position(7, 8));
        assertEquals(3, custom.getTopMostPos(), "Error: The topmost row should be 3.");
        assertEquals(7, custom.getBottomMostPos(), "Error: The bottommost row should be 7.");
        assertEquals(2, custom.getLeftMostPos(), "Error: The leftmost column should be 2.");
        assertEquals(8, custom.getRightMostPos(), "Error: The rightmost column should be 8.");
    }

    @Test
    @DisplayName("stillFloating: navio com algumas posições atingidas continua a flutuar")
    void testStillFloatingPartiallyHit() {
        Ship caravel = new Caravel(Compass.NORTH, new Position(2, 2));
        caravel.shoot(new Position(2, 2));
        assertTrue(caravel.stillFloating(), "Error: Ship should float while one position is intact.");
    }

    @Test
    @DisplayName("shoot atinge só a posição alvo de um navio maior")
    void testShootMultiPosition() {
        Ship caravel = new Caravel(Compass.EAST, new Position(2, 2));
        caravel.shoot(new Position(2, 3));
        assertFalse(caravel.getPositions().get(0).isHit(), "Error: The first position should not be hit.");
        assertTrue(caravel.getPositions().get(1).isHit(), "Error: The second position should be hit.");
    }

    @Test
    @DisplayName("getAdjacentPositions de um navio maior não repete nem inclui as suas posições")
    void testGetAdjacentPositionsMultiPosition() {
        Ship caravel = new Caravel(Compass.NORTH, new Position(5, 5));
        List<IPosition> adjacents = caravel.getAdjacentPositions();
        assertEquals(10, adjacents.size(), "Error: A vertical caravel in the middle should have 10 adjacent positions.");
        assertFalse(adjacents.contains(new Position(5, 5)), "Error: The ship's own positions should not be adjacent.");
        assertFalse(adjacents.contains(new Position(6, 5)), "Error: The ship's own positions should not be adjacent.");
        assertTrue(adjacents.contains(new Position(7, 6)), "Error: (7, 6) should be adjacent.");
    }

    @Test
    @DisplayName("tooCloseTo(IShip) verifica todas as posições do outro navio")
    void testTooCloseToShipLastPosition() {
        Ship caravel = new Caravel(Compass.EAST, new Position(5, 3));
        assertTrue(ship.tooCloseTo(caravel), "Error: Only the caravel's second position (5, 4) is adjacent, but it should be detected.");
        Ship farCaravel = new Caravel(Compass.EAST, new Position(5, 0));
        assertFalse(ship.tooCloseTo(farCaravel), "Error: A caravel at (5,0)-(5,1) is not close to (5,5).");
    }

    @Test
    @DisplayName("sink atinge todas as posições e o navio deixa de flutuar")
    void testSink() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(2, 2));
        galleon.sink();
        for (IPosition position : galleon.getPositions())
            assertTrue(position.isHit(), "Error: Every position should be hit after sink().");
        assertFalse(galleon.stillFloating(), "Error: Ship should not float after sink().");
    }

    @Test
    @DisplayName("toString mostra categoria, orientação e posição de origem")
    void testToString() {
        assertEquals("[Barca n F6]", ship.toString(), "Error: Wrong string representation.");
        assertEquals("[Caravela e A1]", new Caravel(Compass.EAST, new Position(0, 0)).toString(), "Error: Wrong string representation.");
    }

    /**
     * Returns true when the JVM runs with assertions enabled (-ea),
     * which is the default for IntelliJ and for Maven Surefire.
     */
    private static boolean assertionsEnabled() {
        boolean enabled = false;
        assert enabled = true;
        return enabled;
    }

    @Test
    @DisplayName("buildShip cria o navio certo para cada nome, mantém orientação e origem, e devolve null para nomes desconhecidos")
    void testBuildShip() {
        {
            Position origin = new Position(1, 1);
            assertTrue(Ship.buildShip("barca", Compass.NORTH, origin) instanceof Barge, "Error: 'barca' should build a Barge.");
            assertTrue(Ship.buildShip("caravela", Compass.NORTH, origin) instanceof Caravel, "Error: 'caravela' should build a Caravel.");
            assertTrue(Ship.buildShip("nau", Compass.NORTH, origin) instanceof Carrack, "Error: 'nau' should build a Carrack.");
            assertTrue(Ship.buildShip("fragata", Compass.NORTH, origin) instanceof Frigate, "Error: 'fragata' should build a Frigate.");
            assertTrue(Ship.buildShip("galeao", Compass.NORTH, origin) instanceof Galleon, "Error: 'galeao' should build a Galleon.");
        }
        {
            assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(1, 1)), "Error: An unknown kind should return null.");
        }
        {
            Position origin = new Position(3, 4);
            Ship built = Ship.buildShip("fragata", Compass.EAST, origin);
            assertEquals(Compass.EAST, built.getBearing(), "Error: The built ship should keep the bearing.");
            assertSame(origin, built.getPosition(), "Error: The built ship should keep the origin.");
            assertEquals(4, built.getSize(), "Error: A frigate should have size 4.");
        }
    }

    @Test
    @DisplayName("isInsideBoard: navio dentro do tabuleiro e navios que saem por cada um dos 4 lados")
    void testIsInsideBoard() {
        {
            assertTrue(ship.isInsideBoard(), "Error: Barge at (5,5) should be inside the board.");
            assertTrue(new Barge(Compass.NORTH, new Position(0, 0)).isInsideBoard(), "Error: Barge at (0,0) should be inside the board.");
            assertTrue(new Caravel(Compass.EAST, new Position(9, 8)).isInsideBoard(), "Error: Caravel touching the bottom-right corner should be inside.");
        }
        {
            assertFalse(new Barge(Compass.NORTH, new Position(0, -1)).isInsideBoard(), "Error: Ship left of the board should be outside.");
            assertFalse(new Caravel(Compass.EAST, new Position(0, Game.BOARD_SIZE - 1)).isInsideBoard(), "Error: Ship crossing the right edge should be outside.");
            assertFalse(new Barge(Compass.NORTH, new Position(-1, 0)).isInsideBoard(), "Error: Ship above the board should be outside.");
            assertFalse(new Caravel(Compass.NORTH, new Position(Game.BOARD_SIZE - 1, 0)).isInsideBoard(), "Error: Ship crossing the bottom edge should be outside.");
        }
    }

    @Test
    @DisplayName("buildShip, occupies, tooCloseTo e shoot rejeitam (assert) argumentos nulos ou tiros fora do tabuleiro")
    void testAssertions() {
        if (!assertionsEnabled()) return;
        {
            Position origin = new Position(1, 1);
            assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, origin), "Error: null kind should fail the assertion.");
            assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, origin), "Error: null bearing should fail the assertion.");
            assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null), "Error: null position should fail the assertion.");
        }
        {
            assertThrows(AssertionError.class, () -> ship.occupies(null), "Error: occupies(null) should fail the assertion.");
            assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null), "Error: tooCloseTo((IShip) null) should fail the assertion.");
            assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null), "Error: tooCloseTo((IPosition) null) should fail the assertion.");
            assertThrows(AssertionError.class, () -> ship.shoot(null), "Error: shoot(null) should fail the assertion.");
        }
        {
            assertThrows(AssertionError.class, () -> ship.shoot(new Position(-1, 0)), "Error: A shot outside the board should fail the assertion.");
        }
    }
}
