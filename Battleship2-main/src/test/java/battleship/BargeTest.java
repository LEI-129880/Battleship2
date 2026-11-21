package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Barge class.
 * Author: britoeabreu
 * Date: 2025-11-11 16:40
 * Cyclomatic Complexity:
 * - Constructor: 1
 * - getSize: 1
 * - stillFloating: 2
 * - getPositions: 1
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 1
 * - getBottomMostPos: 1
 * - getLeftMostPos: 1
 * - getRightMostPos: 1
 */
@DisplayName("Testes da classe Barge")
public class BargeTest {

	private Barge barge;

	@BeforeEach
	void setUp() {
		barge = new Barge(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		barge = null;
	}

	@Test
	@DisplayName("O construtor cria uma barca com 1 posição na origem")
	void constructor() {
		assertNotNull(barge, "Error: Barge instance should not be null.");
		assertEquals("Barca", barge.getCategory(), "Error: Barge category should be 'Barca'.");
		assertEquals(Compass.NORTH, barge.getBearing(), "Error: Barge bearing is incorrect.");
		assertEquals(1, barge.getSize(), "Error: Barge size should be 1.");
		assertEquals(1, barge.getPositions().size(), "Error: Barge should have exactly one position.");
		assertEquals(new Position(5, 5), barge.getPositions().get(0), "Error: Barge position is incorrect.");
	}

	@Test
	@DisplayName("getSize devolve 1")
	void getSize() {
		assertEquals(1, barge.getSize(), "Error: Barge size should be 1.");
	}

	@Test
	@DisplayName("stillFloating: a barca intacta continua a flutuar")
	void stillFloating1() {
		assertTrue(barge.stillFloating(), "Error: Barge should still be floating.");
	}

	@Test
	@DisplayName("stillFloating: a barca atingida afunda")
	void stillFloating2() {
		barge.getPositions().get(0).shoot();
		assertFalse(barge.stillFloating(), "Error: Barge should not float after being hit.");
	}

	@Test
	@DisplayName("getPositions devolve a única posição da barca")
	void getPositions() {
		assertEquals(1, barge.getPositions().size(), "Error: Barge should have exactly one position.");
		assertEquals(new Position(5, 5), barge.getPositions().get(0), "Error: Barge position is incorrect.");
	}

	@Test
	@DisplayName("shoot na posição da barca atinge-a")
	void shoot1() {
		barge.shoot(new Position(5, 5));
		assertTrue(barge.getPositions().get(0).isHit(), "Error: The position should be hit.");
	}

	@Test
	@DisplayName("shoot noutra posição não atinge a barca")
	void shoot2() {
		barge.shoot(new Position(1, 1));
		assertFalse(barge.getPositions().get(0).isHit(), "Error: Shot should not affect the barge for invalid positions.");
	}

	@Test
	@DisplayName("occupies: a barca ocupa a sua posição")
	void occupies1() {
		assertTrue(barge.occupies(new Position(5, 5)), "Error: Barge should occupy position (5, 5).");
	}

	@Test
	@DisplayName("occupies: a barca não ocupa outra posição")
	void occupies2() {
		assertFalse(barge.occupies(new Position(1, 1)), "Error: Barge should not occupy position (1, 1).");
	}

	@Test
	@DisplayName("tooCloseTo(IShip): barca vizinha está demasiado perto")
	void tooCloseToShip1() {
		Barge nearbyBarge = new Barge(Compass.NORTH, new Position(5, 6));
		assertTrue(barge.tooCloseTo(nearbyBarge), "Error: Barges should be too close.");
	}

	@Test
	@DisplayName("tooCloseTo(IShip): barca distante não está demasiado perto")
	void tooCloseToShip2() {
		Barge distantBarge = new Barge(Compass.NORTH, new Position(10, 10));
		assertFalse(barge.tooCloseTo(distantBarge), "Error: Barges should not be too close.");
	}

	@Test
	@DisplayName("tooCloseTo(IPosition): posição vizinha está demasiado perto")
	void tooCloseToPosition1() {
		assertTrue(barge.tooCloseTo(new Position(5, 6)), "Error: Barge should be too close to adjacent position (5, 6).");
	}

	@Test
	@DisplayName("tooCloseTo(IPosition): posição distante não está demasiado perto")
	void tooCloseToPosition2() {
		assertFalse(barge.tooCloseTo(new Position(7, 7)), "Error: Barge should not be too close to non-adjacent position (7, 7).");
	}

	@Test
	@DisplayName("getTopMostPos devolve a linha da barca")
	void getTopMostPos() {
		assertEquals(5, barge.getTopMostPos(), "Error: The topmost position should be 5.");
	}

	@Test
	@DisplayName("getBottomMostPos devolve a linha da barca")
	void getBottomMostPos() {
		assertEquals(5, barge.getBottomMostPos(), "Error: The bottommost position should be 5.");
	}

	@Test
	@DisplayName("getLeftMostPos devolve a coluna da barca")
	void getLeftMostPos() {
		assertEquals(5, barge.getLeftMostPos(), "Error: The leftmost position should be 5.");
	}

	@Test
	@DisplayName("getRightMostPos devolve a coluna da barca")
	void getRightMostPos() {
		assertEquals(5, barge.getRightMostPos(), "Error: The rightmost position should be 5.");
	}

	@Test
	@DisplayName("O construtor lança NullPointerException com argumentos nulos")
	void constructorNullInput() {
		assertThrows(NullPointerException.class, () -> new Barge(null, null),
				"Error: NullPointerException should be thrown for null input.");
		assertThrows(NullPointerException.class, () -> new Barge(Compass.NORTH, null),
				"Error: NullPointerException should be thrown for null position.");
	}

	@Test
	@DisplayName("A barca ocupa uma única posição em qualquer orientação")
	void constructorAllBearings() {
		for (Compass bearing : Compass.values()) {
			Barge b = new Barge(bearing, new Position(2, 7));
			assertEquals(bearing, b.getBearing(), "Error: Barge bearing is incorrect.");
			assertEquals(1, b.getPositions().size(), "Error: Barge should have exactly one position for " + bearing.name());
			assertEquals(new Position(2, 7), b.getPositions().get(0), "Error: Barge position is incorrect for " + bearing.name());
		}
	}

	@Test
	@DisplayName("A posição da barca é uma cópia, não a posição recebida")
	void positionIsCopied() {
		Position origin = new Position(5, 5);
		Barge b = new Barge(Compass.NORTH, origin);
		assertSame(origin, b.getPosition(), "Error: getPosition should return the origin given to the constructor.");
		assertNotSame(origin, b.getPositions().get(0), "Error: The occupied position should be a new object.");
	}

	@Test
	@DisplayName("getAdjacentPositions: uma barca no meio do tabuleiro tem 8 vizinhas")
	void getAdjacentPositions() {
		assertEquals(8, barge.getAdjacentPositions().size(), "Error: A barge in the middle of the board should have 8 adjacent positions.");
	}

	@Test
	@DisplayName("sink afunda a barca")
	void sink() {
		barge.sink();
		assertTrue(barge.getPositions().get(0).isHit(), "Error: The barge position should be hit after sink().");
		assertFalse(barge.stillFloating(), "Error: Barge should not float after sink().");
	}

	@Test
	@DisplayName("isInsideBoard: barca dentro e fora do tabuleiro")
	void isInsideBoard() {
		assertTrue(barge.isInsideBoard(), "Error: Barge at (5,5) should be inside the board.");
		assertFalse(new Barge(Compass.NORTH, new Position(Game.BOARD_SIZE, 0)).isInsideBoard(),
				"Error: Barge outside the board should not be inside the board.");
	}

	@Test
	@DisplayName("toString mostra categoria, orientação e posição")
	void toStringTest() {
		assertEquals("[Barca n F6]", barge.toString(), "Error: Barge string representation is incorrect.");
	}
}
