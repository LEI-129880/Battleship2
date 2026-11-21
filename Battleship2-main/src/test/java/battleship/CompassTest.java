package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Compass.
 * Author: britoeabreu
 * Date: 2023-10-10
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getDirection: 1
 * - toString: 1
 * - charToCompass: 4
 */
@DisplayName("Testes da enumeração Compass")
public class CompassTest {

	private Compass compass;

	@BeforeEach
	void setUp() {
		compass = Compass.NORTH; // Example instance for testing
	}

	@AfterEach
	void tearDown() {
		compass = null;
	}

	/**
	 * Test for the Compass constructor.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	@DisplayName("Os valores da enumeração existem")
	void constructor() {
		assertNotNull(compass, "Error: Compass instance should not be null.");
		assertEquals(4, Compass.values().length, "Error: Compass should have exactly 4 directions.");
		assertEquals(Compass.WEST, Compass.valueOf("WEST"), "Error: valueOf(\"WEST\") should return WEST.");
	}

	/**
	 * Test for the getDirection method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	@DisplayName("getDirection devolve o carácter de cada direção")
	void getDirection() {
		assertEquals('n', Compass.NORTH.getDirection(), "Error: Direction for NORTH should be 'n'.");
		assertEquals('s', Compass.SOUTH.getDirection(), "Error: Direction for SOUTH should be 's'.");
		assertEquals('e', Compass.EAST.getDirection(), "Error: Direction for EAST should be 'e'.");
		assertEquals('o', Compass.WEST.getDirection(), "Error: Direction for WEST should be 'o'.");
	}

	/**
	 * Test for the toString method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	@DisplayName("toString devolve o carácter de cada direção como texto")
	void toStringTest() {
		assertEquals("n", Compass.NORTH.toString(), "Error: String representation for NORTH should be 'n'.");
		assertEquals("s", Compass.SOUTH.toString(), "Error: String representation for SOUTH should be 's'.");
		assertEquals("e", Compass.EAST.toString(), "Error: String representation for EAST should be 'e'.");
		assertEquals("o", Compass.WEST.toString(), "Error: String representation for WEST should be 'o'.");
	}

	/**
	 * Test for the charToCompass method (all conditions true).
	 * Cyclomatic Complexity: 4
	 */
	@Test
	@DisplayName("charToCompass converte cada carácter válido na direção certa")
	void charToCompass1() {
		assertEquals(Compass.NORTH, Compass.charToCompass('n'), "Error: 'n' should map to Compass.NORTH.");
		assertEquals(Compass.SOUTH, Compass.charToCompass('s'), "Error: 's' should map to Compass.SOUTH.");
		assertEquals(Compass.EAST, Compass.charToCompass('e'), "Error: 'e' should map to Compass.EAST.");
		assertEquals(Compass.WEST, Compass.charToCompass('o'), "Error: 'o' should map to Compass.WEST.");
	}

	/**
	 * Test for the charToCompass method (invalid input).
	 */
	@Test
	@DisplayName("charToCompass devolve null para um carácter inválido")
	void charToCompass2() {
		assertNull(Compass.charToCompass('x'), "Error: 'x' should map to null.");
	}

	/**
	 * Test for the charToCompass method (null input).
	 */
	@Test
	@DisplayName("charToCompass devolve null para o carácter nulo")
	void charToCompass3() {
		assertNull(Compass.charToCompass('\0'), "Error: Null character should map to null.");
	}

	@Test
	@DisplayName("charToCompass é sensível a maiúsculas ('N' não é válido)")
	void charToCompassUpperCase() {
		assertNull(Compass.charToCompass('N'), "Error: 'N' (upper case) should map to null.");
	}

	@Test
	@DisplayName("charToCompass é o inverso de getDirection para todas as direções")
	void charToCompassRoundTrip() {
		for (Compass direction : Compass.values())
			assertEquals(direction, Compass.charToCompass(direction.getDirection()),
					"Error: charToCompass(getDirection()) should return the same direction for " + direction.name());
	}

	@Test
	@DisplayName("randomBearing devolve sempre uma direção válida")
	void randomBearing() {
		for (int i = 0; i < 200; i++) {
			Compass random = Compass.randomBearing();
			assertNotNull(random, "Error: randomBearing should never return null.");
			assertEquals(random, Compass.valueOf(random.name()), "Error: randomBearing returned an unknown direction.");
		}
	}
}
