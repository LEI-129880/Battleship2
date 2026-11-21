
package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameAdditionalTest {

    @Test
    @DisplayName("Random enemy fire produces three different valid shots")
    void testRandomEnemyFire() throws Exception {
        Game game = new Game(new Fleet());

        String json = game.randomEnemyFire();

        JsonNode shots = new ObjectMapper().readTree(json);

        assertEquals(Game.NUMBER_SHOTS, shots.size());
        assertEquals(1, game.getAlienMoves().size());
        assertEquals(3, game.getAlienMoves().get(0).getShots().size());

        Set<IPosition> unique = new HashSet<>(
                game.getAlienMoves().get(0).getShots());

        assertEquals(3, unique.size());

        for (IPosition position : unique) {
            assertTrue(position.isInside());
        }
    }

    @Test
    @DisplayName("Random enemy fire avoids previously fired positions")
    void testRandomEnemyFireAvoidsPreviousShots() {
        Game game = new Game(new Fleet());

        game.fireShots(List.of(
                new Position(0, 0),
                new Position(0, 1),
                new Position(0, 2)
        ));

        List<IPosition> previous =
                game.getAlienMoves().get(0).getShots();

        game.randomEnemyFire();

        List<IPosition> generated =
                game.getAlienMoves().get(1).getShots();

        assertEquals(3, generated.size());

        for (IPosition position : generated) {
            assertFalse(previous.contains(position));
        }

        assertEquals(0, game.getRepeatedShots());
    }

    @Test
    @DisplayName("Board displays floating ships and marks successful shots")
    void testPrintBoardWithShipAndHit() {
        Fleet fleet = new Fleet();

        IShip ship = new Barge(
                Compass.NORTH, new Position(4, 4));

        assertTrue(fleet.addShip(ship));

        Game game = new Game(fleet);

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(output));

            game.printMyBoard(false, false);

            String before = output.toString();
            assertTrue(before.contains("#"));

            output.reset();

            game.fireShots(List.of(
                    new Position(4, 4),
                    new Position(0, 0),
                    new Position(0, 1)
            ));

            game.printMyBoard(true, true);

            String after = output.toString();

            assertTrue(after.contains("*"));
            assertTrue(after.contains("o"));
            assertTrue(after.contains("LEGENDA"));

        } finally {
            System.setOut(original);
        }
    }

    @Test
    @DisplayName("Board shows adjacent positions of sunk ships")
    void testPrintBoardWithSunkShip() {
        Fleet fleet = new Fleet();

        IShip ship = new Barge(
                Compass.NORTH, new Position(4, 4));

        assertTrue(fleet.addShip(ship));

        ship.sink();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(output));

            Game.printBoard(fleet, List.of(), false, false);

            String result = output.toString();

            assertTrue(result.contains("#"));
            assertTrue(result.contains("-"));

        } finally {
            System.setOut(original);
        }
    }

    @Test
    @DisplayName("Game initializes both fleets and move collections")
    void testGameCollections() {
        Fleet fleet = new Fleet();
        Game game = new Game(fleet);

        assertSame(fleet, game.getMyFleet());
        assertNotNull(game.getAlienFleet());
        assertNotNull(game.getMyMoves());
        assertNotNull(game.getAlienMoves());

        assertTrue(game.getAlienFleet().getShips().isEmpty());
        assertTrue(game.getMyMoves().isEmpty());
        assertTrue(game.getAlienMoves().isEmpty());
    }
}
