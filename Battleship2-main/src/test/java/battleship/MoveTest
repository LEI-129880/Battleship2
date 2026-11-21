
package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode process(Move move, boolean verbose)
            throws Exception {
        return mapper.readTree(move.processEnemyFire(verbose));
    }

    @Test
    @DisplayName("Move constructor stores number, shots and results")
    void testConstructorAndGetters() {
        List<IPosition> shots = List.of(new Position(1, 2));
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(5, shots, results);

        assertEquals(5, move.getNumber());
        assertSame(shots, move.getShots());
        assertSame(results, move.getShotResults());
    }

    @Test
    @DisplayName("Move string contains number and list sizes")
    void testToString() {
        Move move = new Move(
                3,
                List.of(new Position(0, 0)),
                List.of(new IGame.ShotResult(true, false, null, false))
        );

        String text = move.toString();

        assertTrue(text.contains("number=3"));
        assertTrue(text.contains("shots=1"));
        assertTrue(text.contains("results=1"));
    }

    @Test
    @DisplayName("Reading a move from scanner parses all positions")
    void testReadMove() {
        Scanner scanner = new Scanner("2 1 2 3 4");

        Move move = IMove.readMove(7, scanner);

        assertEquals(7, move.getNumber());
        assertEquals(2, move.getShots().size());
        assertEquals(1, move.getShots().get(0).getRow());
        assertEquals(2, move.getShots().get(0).getColumn());
        assertEquals(3, move.getShots().get(1).getRow());
        assertEquals(4, move.getShots().get(1).getColumn());
        assertTrue(move.getShotResults().isEmpty());
    }

    @Test
    @DisplayName("Empty results count all shots as outside")
    void testEmptyResults() throws Exception {
        Move move = new Move(1, List.of(), List.of());

        JsonNode json = process(move, false);

        assertEquals(0, json.get("validShots").asInt());
        assertEquals(0, json.get("repeatedShots").asInt());
        assertEquals(0, json.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                json.get("outsideShots").asInt());
        assertEquals(0, json.get("sunkBoats").size());
        assertEquals(0, json.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Valid shots without ships are counted as misses")
    void testMissedShots() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(2, List.of(), results);

        JsonNode json = process(move, true);

        assertEquals(3, json.get("validShots").asInt());
        assertEquals(3, json.get("missedShots").asInt());
        assertEquals(0, json.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Repeated shots are counted separately")
    void testRepeatedShots() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(3, List.of(), results);

        JsonNode json = process(move, true);

        assertEquals(0, json.get("validShots").asInt());
        assertEquals(3, json.get("repeatedShots").asInt());
        assertEquals(0, json.get("missedShots").asInt());
        assertEquals(0, json.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Invalid shots are excluded from valid shots")
    void testInvalidShots() throws Exception {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(4, List.of(), results);

        JsonNode json = process(move, true);

        assertEquals(1, json.get("validShots").asInt());
        assertEquals(1, json.get("missedShots").asInt());
        assertEquals(2, json.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Hits on a floating ship are grouped by category")
    void testHitsOnBoats() throws Exception {
        IShip ship = new Barge(
                Compass.NORTH, new Position(1, 1));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, false),
                new IGame.ShotResult(true, false, ship, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(5, List.of(), results);

        JsonNode json = process(move, true);

        assertEquals(3, json.get("validShots").asInt());
        assertEquals(1, json.get("missedShots").asInt());
        assertEquals(1, json.get("hitsOnBoats").size());
        assertEquals(ship.getCategory(),
                json.get("hitsOnBoats").get(0).get("type").asText());
        assertEquals(2,
                json.get("hitsOnBoats").get(0).get("hits").asInt());
        assertEquals(0, json.get("sunkBoats").size());
    }

    @Test
    @DisplayName("Sunk ships are reported separately from floating hits")
    void testSunkBoats() throws Exception {
        IShip ship = new Barge(
                Compass.NORTH, new Position(1, 1));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, true),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(6, List.of(), results);

        JsonNode json = process(move, true);

        assertEquals(1, json.get("sunkBoats").size());
        assertEquals(ship.getCategory(),
                json.get("sunkBoats").get(0).get("type").asText());
        assertEquals(1,
                json.get("sunkBoats").get(0).get("count").asInt());
        assertEquals(0, json.get("hitsOnBoats").size());
        assertEquals(1, json.get("repeatedShots").asInt());
        assertEquals(1, json.get("missedShots").asInt());
    }
}
