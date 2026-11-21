package battleship;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class TasksTest {

    @Test
    void consoleCommandsShareTheGraphicalSessionAndAdvanceClock() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
        var originalInput = System.in;
        var originalOutput = System.out;
        var output = new java.io.ByteArrayOutputStream();
        try {
            System.setIn(new java.io.ByteArrayInputStream(
                    "tabuleiro\ngerafrota\nrajada A1 B2 C3\ntiros\nestado\nmapa\ndesisto\n"
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            System.setOut(new java.io.PrintStream(output, true, java.nio.charset.StandardCharsets.UTF_8));
            Tasks.menu();
            javax.swing.SwingUtilities.invokeAndWait(() -> {});
            javax.swing.SwingUtilities.invokeAndWait(() -> {});
            String text = output.toString(java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(text.contains("Tempo da jogada 1:"));
            assertTrue(text.contains("Bons ventos!"));
            assertFalse(text.contains("Gera ou coloca primeiro"));
            assertFalse(text.contains("Comando desconhecido"));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    @Test
    void parsesCompactAndSpacedShotsAndRejectsBadInput() {
        assertEquals(Tasks.parseShots("A1 B2 C3"), Tasks.parseShots("A 1 b 2 C 3"));
        assertThrows(IllegalArgumentException.class, () -> Tasks.parseShots("A1 B2"));
        assertThrows(IllegalArgumentException.class, () -> Tasks.parseShots("A1 B2 Z30"));
        assertThrows(IllegalArgumentException.class, () -> Tasks.parseShots("A1 B2 C3 D4"));
    }

	  @Test
    @DisplayName("Read numeric row and column")
    void readPosition() {
        Position position = Tasks.readPosition(
                new Scanner("3 5"));

        assertEquals(3, position.getRow());
        assertEquals(5, position.getColumn());
    }

    @Test
    @DisplayName("Read valid ship from scanner")
    void readShip() {
        Ship ship = Tasks.readShip(
                new Scanner("barca 2 3 n"));

        assertNotNull(ship);
        assertInstanceOf(Barge.class, ship);
        assertEquals(2, ship.getPosition().getRow());
        assertEquals(3, ship.getPosition().getColumn());
        assertEquals(Compass.NORTH, ship.getBearing());
    }

    @Test
    @DisplayName("Unknown ship category returns null")
    void taskA() {
        Ship ship = Tasks.readShip(
                new Scanner("unknown 2 3 n"));

        assertNull(ship);
    }

    @Test
    @DisplayName("Read compact and spaced classic positions")
    void taskB() {
        IPosition compact = Tasks.readClassicPosition(
                new Scanner("A3"));

        IPosition spaced = Tasks.readClassicPosition(
                new Scanner("a 3"));

        assertEquals(compact, spaced);
        assertEquals(0, compact.getRow());
        assertEquals(2, compact.getColumn());
    }

    @Test
    @DisplayName("Reject invalid classic position formats")
    void taskC() {
        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(
                        new Scanner("")));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(
                        new Scanner("invalid")));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(
                        new Scanner("123")));
    }

    @Test
    @DisplayName("Menu help prints available commands")
    void taskD() {
        PrintStream original = System.out;
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));

            Tasks.menuHelp();

            String result = output.toString();

            assertNotNull(result);
            assertFalse(result.isBlank(),
                    "Menu help should print instructions");

        } finally {
            System.setOut(original);
        }
    }
    
    @Test
    @DisplayName("Build a complete fleet with eleven valid ships")
    void testBuildFleetComplete() {
        StringBuilder input = new StringBuilder();

        for (int row = 0; row <= 6; row += 3) {
            for (int col = 0; col <= 9; col += 3) {
                if ((row == 6 && col == 6)
                        || (row == 6 && col == 9)) {
                    continue;
                }

                input.append("barca ")
                        .append(row).append(" ")
                        .append(col).append(" n\n");
            }
        }

        // Add the eleventh ship
        input.append("barca 6 6 n\n");

        Fleet fleet = Tasks.buildFleet(
                new Scanner(input.toString()));

        assertNotNull(fleet);
        assertEquals(11, fleet.getShips().size());
    }

    @Test
    @DisplayName("Build fleet ignores unknown and overlapping ships")
    void testBuildFleetInvalidShips() {
        StringBuilder input = new StringBuilder();

        input.append("unknown 0 0 n\n");
        input.append("barca 0 0 n\n");
        input.append("barca 0 0 n\n");

        int count = 1;

        for (int row = 0; row <= 6 && count < 11; row += 3) {
            for (int col = 0; col <= 9 && count < 11; col += 3) {
                if (row == 0 && col == 0) {
                    continue;
                }

                input.append("barca ")
                        .append(row).append(" ")
                        .append(col).append(" n\n");
                count++;
            }
        }

        Fleet fleet = Tasks.buildFleet(
                new Scanner(input.toString()));

        assertEquals(11, fleet.getShips().size());
    }

    @Test
    @DisplayName("Parse shots accepts lowercase and spaced coordinates")
    void testParseShotsFormats() {
        var compact = Tasks.parseShots("a1 b2 c3");
        var spaced = Tasks.parseShots("A 1 B 2 C 3");

        assertEquals(3, compact.size());
        assertEquals(compact, spaced);

        assertEquals(new Position(0, 0), compact.get(0));
        assertEquals(new Position(1, 1), compact.get(1));
        assertEquals(new Position(2, 2), compact.get(2));
    }

    @Test
    @DisplayName("Parse shots rejects invalid positions and shot counts")
    void testParseShotsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> Tasks.parseShots(""));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.parseShots("A1"));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.parseShots("A1 B2 C3 D4"));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.parseShots("A1 B2 K11"));

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.parseShots("A1 B2 invalid"));
    }

}
