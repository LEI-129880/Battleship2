package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TasksTest
 *
 * @author Your Name
 * Date: 07/01/2026
 * Time: 00:53
 */
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

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void taskA() {
	}

	@Test
	void taskB() {
	}

	@Test
	void taskC() {
	}

	@Test
	void taskD() {
	}

	@Test
	void buildFleet() {
	}

	@Test
	void readShip() {
	}

	@Test
	void readPosition() {
	}

	@Test
	void firingRound() {
	}
}
