package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for the Galleon class.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 5
 * - stillFloating: 2
 * - getPositions: 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
@DisplayName("Testes da classe Galleon")
public class GalleonTest {

	private Galleon galleon;

	@BeforeEach
	void setUp() {
		galleon = new Galleon(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		galleon = null;
	}

	/**
	 * Test for the constructor with NORTH direction.
	 * Cyclomatic Complexity: 5
	 */
	@Test
	@DisplayName("Virada a NORTE forma um T com a barra em cima")
	void testConstructorNorth() {
		List<IPosition> positions = galleon.getPositions();
		assertNotNull(galleon, "Error: Galleon instance should not be null.");
		assertEquals(5, positions.size(), "Error: Galleon should have exactly 5 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for NORTH.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: The second position is incorrect for NORTH.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: The third position is incorrect for NORTH.");
		assertEquals(new Position(6, 6), positions.get(3), "Error: The fourth position is incorrect for NORTH.");
		assertEquals(new Position(7, 6), positions.get(4), "Error: The fifth position is incorrect for NORTH.");
	}

	/**
	 * Test for the constructor with SOUTH direction.
	 */
	@Test
	@DisplayName("Virada a SUL forma um T invertido com a barra em baixo")
	void testConstructorSouth() {
		Galleon southGalleon = new Galleon(Compass.SOUTH, new Position(5, 5));
		List<IPosition> positions = southGalleon.getPositions();
		assertNotNull(southGalleon, "Error: Galleon instance should not be null.");
		assertEquals(5, positions.size(), "Error: Galleon should have exactly 5 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for SOUTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: The second position is incorrect for SOUTH.");
		assertEquals(new Position(7, 4), positions.get(2), "Error: The third position is incorrect for SOUTH.");
		assertEquals(new Position(7, 5), positions.get(3), "Error: The fourth position is incorrect for SOUTH.");
		assertEquals(new Position(7, 6), positions.get(4), "Error: The fifth position is incorrect for SOUTH.");
	}

	/**
	 * Test for the stillFloating method (all positions intact).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	@DisplayName("stillFloating: o galeão intacto flutua")
	void testStillFloating1() {
		assertTrue(galleon.stillFloating(), "Error: Galleon should initially be floating.");
	}

	/**
	 * Test for the stillFloating method (all positions hit).
	 */
	@Test
	@DisplayName("stillFloating: o galeão com todas as posições atingidas afunda")
	void testStillFloating2() {
		galleon.getPositions().forEach(pos -> pos.shoot());
		assertFalse(galleon.stillFloating(), "Error: Galleon should not float when all positions are hit.");
	}

	/**
	 * Test for the getTopMostPos method.
	 * Cyclomatic Complexity: 2
	 */
	@Test
	@DisplayName("getTopMostPos devolve a linha de cima")
	void testGetTopMostPos() {
		assertEquals(5, galleon.getTopMostPos(), "Error: The topmost position should be 5.");
	}

	/**
	 * Test for the getBottomMostPos method.
	 * Cyclomatic Complexity: 2
	 */
	@Test
	@DisplayName("getBottomMostPos devolve a linha de baixo")
	void testGetBottomMostPos() {
		assertEquals(7, galleon.getBottomMostPos(), "Error: The bottommost position should be 7.");
	}

	/**
	 * Test for the getLeftMostPos method.
	 * Cyclomatic Complexity: 2
	 */
	@Test
	@DisplayName("getLeftMostPos devolve a coluna da esquerda")
	void testGetLeftMostPos() {
		assertEquals(5, galleon.getLeftMostPos(), "Error: The leftmost position should be 5.");
	}

	/**
	 * Test for the getRightMostPos method.
	 * Cyclomatic Complexity: 2
	 */
	@Test
	@DisplayName("getRightMostPos devolve a coluna da direita")
	void testGetRightMostPos() {
		assertEquals(7, galleon.getRightMostPos(), "Error: The rightmost position should be 7.");
	}

	/**
	 * Test for the constructor with invalid input.
	 */
	@Test
	@DisplayName("O construtor lança NullPointerException com argumentos nulos")
	void testConstructorWithInvalidInput() {
		assertThrows(NullPointerException.class, () -> new Galleon(null, null),
				"Error: NullPointerException should have been thrown for null input.");
		assertThrows(NullPointerException.class, () -> new Galleon(Compass.NORTH, null),
				"Error: NullPointerException should have been thrown when position is null.");
	}

	/**
	 * Test for the constructor with EAST direction.
	 */
	@Test
	@DisplayName("Virada a ESTE ocupa as posições esperadas")
	void testConstructorEast() {
		Galleon eastGalleon = new Galleon(Compass.EAST, new Position(5, 5));
		List<IPosition> positions = eastGalleon.getPositions();
		assertNotNull(eastGalleon, "Error: Galleon instance should not be null.");
		assertEquals(5, positions.size(), "Error: Galleon should have exactly 5 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for EAST.");
		assertEquals(new Position(6, 3), positions.get(1), "Error: The second position is incorrect for EAST.");
		assertEquals(new Position(6, 4), positions.get(2), "Error: The third position is incorrect for EAST.");
		assertEquals(new Position(6, 5), positions.get(3), "Error: The fourth position is incorrect for EAST.");
		assertEquals(new Position(7, 5), positions.get(4), "Error: The fifth position is incorrect for EAST.");
	}

	/**
	 * Test for the constructor with WEST direction.
	 */
	@Test
	@DisplayName("Virada a OESTE ocupa as posições esperadas")
	void testConstructorWest() {
		Galleon westGalleon = new Galleon(Compass.WEST, new Position(5, 5));
		List<IPosition> positions = westGalleon.getPositions();
		assertNotNull(westGalleon, "Error: Galleon instance should not be null.");
		assertEquals(5, positions.size(), "Error: Galleon should have exactly 5 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: The first position is incorrect for WEST.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: The second position is incorrect for WEST.");
		assertEquals(new Position(6, 6), positions.get(2), "Error: The third position is incorrect for WEST.");
		assertEquals(new Position(6, 7), positions.get(3), "Error: The fourth position is incorrect for WEST.");
		assertEquals(new Position(7, 5), positions.get(4), "Error: The fifth position is incorrect for WEST.");
	}

	@Test
	@DisplayName("Em todas as orientações o galeão tem categoria 'Galeao', 5 posições diferentes e os limites certos")
	void testShapeAndBoundsAllBearings() {
		assertEquals("Galeao", galleon.getCategory(), "Error: Galleon category should be 'Galeao'.");
		assertEquals(5, galleon.getSize(), "Error: Galleon size should be 5.");
		for (Compass bearing : Compass.values()) {
			Galleon g = new Galleon(bearing, new Position(5, 5));
			assertEquals(bearing, g.getBearing(), "Error: Galleon bearing is incorrect.");
			assertEquals(5, new java.util.HashSet<>(g.getPositions()).size(),
					"Error: Galleon should have 5 different positions for " + bearing.name());
		}
		Galleon eastGalleon = new Galleon(Compass.EAST, new Position(5, 5));
		Galleon southGalleon = new Galleon(Compass.SOUTH, new Position(5, 5));
		Galleon westGalleon = new Galleon(Compass.WEST, new Position(5, 5));
		assertAll("bounds",
				() -> assertEquals(5, eastGalleon.getTopMostPos(), "Error: EAST topmost position should be 5."),
				() -> assertEquals(7, eastGalleon.getBottomMostPos(), "Error: EAST bottommost position should be 7."),
				() -> assertEquals(3, eastGalleon.getLeftMostPos(), "Error: EAST leftmost position should be 3."),
				() -> assertEquals(5, eastGalleon.getRightMostPos(), "Error: EAST rightmost position should be 5."),
				() -> assertEquals(4, southGalleon.getLeftMostPos(), "Error: SOUTH leftmost position should be 4."),
				() -> assertEquals(6, southGalleon.getRightMostPos(), "Error: SOUTH rightmost position should be 6."),
				() -> assertEquals(5, westGalleon.getLeftMostPos(), "Error: WEST leftmost position should be 5."),
				() -> assertEquals(7, westGalleon.getRightMostPos(), "Error: WEST rightmost position should be 7."));
	}

	@Test
	@DisplayName("O galeão flutua com algumas posições atingidas e afunda com sink")
	void testPartialHitsAndSink() {
		galleon.shoot(new Position(5, 5));
		galleon.shoot(new Position(7, 6));
		assertTrue(galleon.stillFloating(), "Error: Galleon should still float with only 2 of 5 positions hit.");
		galleon.sink();
		for (IPosition position : galleon.getPositions())
			assertTrue(position.isHit(), "Error: Every position should be hit after sink().");
		assertFalse(galleon.stillFloating(), "Error: Galleon should not float after sink().");
	}
}
