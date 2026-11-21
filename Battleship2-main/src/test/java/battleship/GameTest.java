package battleship;

import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}
	
    @Test
    @DisplayName("Reject volleys with incorrect number of shots")
    void testInvalidVolleySize() {
        assertThrows(IllegalArgumentException.class,
                () -> game.fireShots(List.of()));

        assertThrows(IllegalArgumentException.class,
                () -> game.fireShots(List.of(
                        new Position(0, 0))));

        assertThrows(IllegalArgumentException.class,
                () -> game.fireShots(List.of(
                        new Position(0, 0),
                        new Position(0, 1),
                        new Position(0, 2),
                        new Position(0, 3))));

        assertTrue(game.getAlienMoves().isEmpty());
    }

    @Test
    @DisplayName("Duplicate shots in one volley are counted")
    void testDuplicateShotsInVolley() {
        Position first = new Position(1, 1);

        game.fireShots(List.of(
                first,
                first,
                new Position(2, 2)));

        assertEquals(1, game.getRepeatedShots());
        assertEquals(1, game.getAlienMoves().size());
        assertEquals(0, game.getInvalidShots());
    }

    @Test
    @DisplayName("Hitting and sinking a barge updates counters")
    void testHitAndSinkBarge() {
        IShip ship = new Barge(
                Compass.NORTH, new Position(1, 1));

        assertTrue(game.getMyFleet().addShip(ship));
        assertEquals(1, game.getRemainingShips());

        game.fireSingleShot(new Position(1, 1), false);

        assertEquals(1, game.getHits());
        assertEquals(1, game.getSunkShips());
        assertEquals(0, game.getRemainingShips());
    }

    @Test
    @DisplayName("Read three valid enemy shots")
    void testReadEnemyFire() throws Exception {
        String json = game.readEnemyFire(
                new Scanner("A1 B2 C3\n"));

        assertNotNull(json);
        assertEquals(3,
                new ObjectMapper().readTree(json).size());
        assertEquals(1, game.getAlienMoves().size());
    }

    @Test
    @DisplayName("Reject incomplete enemy shot input")
    void testReadEnemyFireInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> game.readEnemyFire(
                        new Scanner("A1 B2\n")));

        assertThrows(IllegalArgumentException.class,
                () -> game.readEnemyFire(
                        new Scanner("A 1 B 2 C\n")));

        assertTrue(game.getAlienMoves().isEmpty());
    }

    @Test
    @DisplayName("JSON serialization and board printing work")
    void testJsonAndBoardPrinting() throws Exception {
        String json = Game.jsonShots(List.of(
                new Position(0, 0),
                new Position(1, 1)));

        var parsed = new ObjectMapper().readTree(json);

        assertEquals(2, parsed.size());
        assertEquals("A",
                parsed.get(0).get("row").asText());
        assertEquals(1,
                parsed.get(0).get("column").asInt());

        assertDoesNotThrow(
                () -> game.printMyBoard(false, false));
        assertDoesNotThrow(
                () -> game.printMyBoard(true, true));
        assertDoesNotThrow(
                () -> game.printAlienBoard(false, true));
        assertDoesNotThrow(game::over);
    }

}
