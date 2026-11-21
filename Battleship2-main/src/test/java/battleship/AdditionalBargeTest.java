package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes adicionais de Barge (129890), criados na ficha 4 para maximizar a cobertura.
 * Os testes originais continuam em BargeTest.
 *
 * @author Rafael Balão (129890)
 */
@DisplayName("Testes adicionais da classe Barge")
public class AdditionalBargeTest {

	private Barge barge;

	@BeforeEach
	void setUp() {
		barge = new Barge(Compass.NORTH, new Position(5, 5));
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
