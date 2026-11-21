package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
@DisplayName("Testes da classe abstrata Ship")
public class ShipTest {

    private Ship ship, shipBorder;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
        shipBorder = new Barge(Compass.NORTH, new Position(0, 0));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("O construtor guarda categoria, orientação, tamanho e posições")
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("getCategory devolve a categoria")
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("getSize devolve o tamanho")
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("getBearing devolve a orientação")
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("getPositions devolve as posições ocupadas")
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("stillFloating: navio intacto flutua")
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    @DisplayName("stillFloating: navio com todas as posições atingidas afunda")
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("shoot numa posição do navio atinge-a")
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    @DisplayName("shoot fora do navio não atinge nada")
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("occupies: o navio ocupa a sua posição")
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    @DisplayName("occupies: o navio não ocupa outra posição")
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("tooCloseTo(IShip): navio vizinho está demasiado perto")
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    @DisplayName("tooCloseTo(IShip): navio distante não está demasiado perto")
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("tooCloseTo(IPosition): posição vizinha está demasiado perto")
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    @DisplayName("tooCloseTo(IPosition): posição distante não está demasiado perto")
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getTopMostPos de uma barca")
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getBottomMostPos de uma barca")
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getLeftMostPos de uma barca")
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getRightMostPos de uma barca")
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("getAdjacentPositions de uma barca no meio e no canto")
    void testGetAdjacentPositions() {
        assertEquals(8, ship.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
        assertEquals(3, shipBorder.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
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
    @DisplayName("buildShip cria o tipo de navio certo para cada nome")
    void testBuildShip() {
        Position origin = new Position(1, 1);
        assertTrue(Ship.buildShip("barca", Compass.NORTH, origin) instanceof Barge, "Error: 'barca' should build a Barge.");
        assertTrue(Ship.buildShip("caravela", Compass.NORTH, origin) instanceof Caravel, "Error: 'caravela' should build a Caravel.");
        assertTrue(Ship.buildShip("nau", Compass.NORTH, origin) instanceof Carrack, "Error: 'nau' should build a Carrack.");
        assertTrue(Ship.buildShip("fragata", Compass.NORTH, origin) instanceof Frigate, "Error: 'fragata' should build a Frigate.");
        assertTrue(Ship.buildShip("galeao", Compass.NORTH, origin) instanceof Galleon, "Error: 'galeao' should build a Galleon.");
    }

    @Test
    @DisplayName("buildShip devolve null para um tipo desconhecido")
    void testBuildShipUnknown() {
        assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(1, 1)), "Error: An unknown kind should return null.");
    }

    @Test
    @DisplayName("buildShip passa a orientação e a origem ao navio criado")
    void testBuildShipKeepsBearingAndPosition() {
        Position origin = new Position(3, 4);
        Ship built = Ship.buildShip("fragata", Compass.EAST, origin);
        assertEquals(Compass.EAST, built.getBearing(), "Error: The built ship should keep the bearing.");
        assertSame(origin, built.getPosition(), "Error: The built ship should keep the origin.");
        assertEquals(4, built.getSize(), "Error: A frigate should have size 4.");
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
    @DisplayName("isInsideBoard: navio totalmente dentro do tabuleiro")
    void testIsInsideBoard() {
        assertTrue(ship.isInsideBoard(), "Error: Barge at (5,5) should be inside the board.");
        assertTrue(shipBorder.isInsideBoard(), "Error: Barge at (0,0) should be inside the board.");
        assertTrue(new Caravel(Compass.EAST, new Position(9, 8)).isInsideBoard(), "Error: Caravel touching the bottom-right corner should be inside.");
    }

    @Test
    @DisplayName("isInsideBoard: navio que sai por cada um dos 4 lados está fora")
    void testIsOutsideBoard() {
        assertFalse(new Barge(Compass.NORTH, new Position(0, -1)).isInsideBoard(), "Error: Ship left of the board should be outside.");
        assertFalse(new Caravel(Compass.EAST, new Position(0, Game.BOARD_SIZE - 1)).isInsideBoard(), "Error: Ship crossing the right edge should be outside.");
        assertFalse(new Barge(Compass.NORTH, new Position(-1, 0)).isInsideBoard(), "Error: Ship above the board should be outside.");
        assertFalse(new Caravel(Compass.NORTH, new Position(Game.BOARD_SIZE - 1, 0)).isInsideBoard(), "Error: Ship crossing the bottom edge should be outside.");
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
    @DisplayName("buildShip rejeita (assert) tipo, orientação ou posição nulos")
    void testBuildShipAssertions() {
        if (!assertionsEnabled()) return;
        Position origin = new Position(1, 1);
        assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, origin), "Error: null kind should fail the assertion.");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, origin), "Error: null bearing should fail the assertion.");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null), "Error: null position should fail the assertion.");
    }

    @Test
    @DisplayName("occupies, tooCloseTo e shoot rejeitam (assert) argumentos nulos")
    void testMethodAssertions() {
        if (!assertionsEnabled()) return;
        assertThrows(AssertionError.class, () -> ship.occupies(null), "Error: occupies(null) should fail the assertion.");
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null), "Error: tooCloseTo((IShip) null) should fail the assertion.");
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null), "Error: tooCloseTo((IPosition) null) should fail the assertion.");
        assertThrows(AssertionError.class, () -> ship.shoot(null), "Error: shoot(null) should fail the assertion.");
    }

    @Test
    @DisplayName("shoot rejeita (assert) um tiro fora do tabuleiro")
    void testShootOutsideBoardAssertion() {
        if (!assertionsEnabled()) return;
        assertThrows(AssertionError.class, () -> ship.shoot(new Position(-1, 0)), "Error: A shot outside the board should fail the assertion.");
    }
}
