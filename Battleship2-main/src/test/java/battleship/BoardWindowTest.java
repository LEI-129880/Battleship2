package battleship;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import static org.junit.jupiter.api.Assertions.*;

class BoardWindowTest {
    @Test
    void consoleAndButtonsUseTheSameFleetAndShots() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            BoardWindow window = new BoardWindow();
            try {
                List<JButton> buttons = descendants(window.getContentPane()).stream()
                        .filter(JButton.class::isInstance).map(JButton.class::cast).toList();
                List<JButton> own = buttons.stream().filter(b -> b.getName() != null
                        && b.getName().startsWith("own-")).toList();
                own.get(0).doClick();
                assertEquals(1, window.consoleGame().getMyFleet().getShips().size());
                assertEquals(5, own.stream().filter(b -> b.getToolTipText().endsWith(": Navio")).count(),
                        "One click must place all five cells of the galleon");
                own.get(99).doClick();
                assertEquals(5, own.stream().filter(b -> b.getToolTipText().endsWith(": Navio")).count(),
                        "An invalid placement must not add a partial ship");
                IFleet fleet = Fleet.createRandom();
                window.consoleFleet(fleet);
                assertSame(fleet, window.consoleGame().getMyFleet());
                assertEquals(25, own.stream().filter(b -> b.getToolTipText().endsWith(": Navio")).count());
                window.consoleShots(Tasks.parseShots("A1 B2 C3"));
                assertEquals(1, window.consoleGame().getAlienMoves().size());
                for (int index : new int[] {0, 11, 22})
                    assertFalse(own.get(index).getToolTipText().endsWith(": Água")
                            || own.get(index).getToolTipText().endsWith(": Navio"));
                window.consoleFleet(Fleet.createRandom());
                assertTrue(window.consoleGame().getAlienMoves().isEmpty());
                button(buttons, "Desfazer").doClick();
                assertEquals(10, window.consoleGame().getMyFleet().getShips().size());
                button(buttons, "Nova partida").doClick();
                assertTrue(window.consoleGame().getMyFleet().getShips().isEmpty());
            } finally { window.dispose(); }
        });
    }

    @Test
    void opponentRespondsAndUnlocksTheNextTurn() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        java.util.concurrent.CompletableFuture<Void> finished = new java.util.concurrent.CompletableFuture<>();
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            BoardWindow window = new BoardWindow();
            try {
                List<JButton> buttons = descendants(window.getContentPane()).stream()
                        .filter(JButton.class::isInstance).map(JButton.class::cast).toList();
                List<JButton> enemy = buttons.stream().filter(b -> b.getName() != null && b.getName().startsWith("enemy-")).toList();
                List<JButton> own = buttons.stream().filter(b -> b.getName() != null && b.getName().startsWith("own-")).toList();
                button(buttons, "Gerar frota").doClick();
                button(buttons, "Começar partida").doClick();
                for (int i = 0; i < 3; i++) enemy.get(i).doClick();
                button(buttons, "Disparar rajada").doClick();
                javax.swing.Timer check = new javax.swing.Timer(1100, event -> {
                    try {
                        assertEquals(97, enemy.stream().filter(JButton::isEnabled).count());
                        assertEquals(3, own.stream().filter(b -> b.getToolTipText().contains("Tiro na água")
                                || b.getToolTipText().contains("atingido") || b.getToolTipText().contains("afundado")).count());
                        render(window, "board-playing-preview.png");
                        finished.complete(null);
                    } catch (Throwable failure) {
                        finished.completeExceptionally(failure);
                    } finally {
                        window.dispose();
                    }
                });
                check.setRepeats(false);
                check.start();
            } catch (Throwable failure) {
                window.dispose();
                finished.completeExceptionally(failure);
            }
        });
        finished.get(10, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Test
    void placesShipsAndShowsTwoBoardsWithHiddenEnemy() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            FlatLightLaf.setup();
            BoardWindow window = new BoardWindow();
            try {
                window.validate();
                List<Component> components = descendants(window.getContentPane());
                List<JButton> buttons = components.stream().filter(JButton.class::isInstance)
                        .map(JButton.class::cast).toList();
                List<JButton> own = buttons.stream().filter(b -> b.getName() != null && b.getName().startsWith("own-")).toList();
                List<JButton> enemy = buttons.stream().filter(b -> b.getName() != null && b.getName().startsWith("enemy-")).toList();
                assertEquals(100, own.size());
                assertEquals(100, enemy.size());
                JButton start = button(buttons, "Começar partida");
                JButton fire = button(buttons, "Disparar rajada");
                assertFalse(start.isEnabled());
                assertFalse(fire.isEnabled());
                assertTrue(enemy.stream().noneMatch(JButton::isEnabled));
                render(window, "board-placement-preview.png");
                own.get(0).doClick();
                assertTrue(labels(components).stream().anyMatch(s -> s.equals("1 de 11 colocados")));
                own.get(1).doClick();
                assertTrue(labels(components).stream().anyMatch(s -> s.equals("1 de 11 colocados")));
                button(buttons, "Desfazer").doClick();
                assertTrue(labels(components).stream().anyMatch(s -> s.equals("0 de 11 colocados")));
                button(buttons, "Gerar frota").doClick();
                assertTrue(start.isEnabled());
                render(window, "board-ready-preview.png");
                start.doClick();
                assertTrue(own.stream().noneMatch(JButton::isEnabled));
                assertTrue(enemy.stream().allMatch(b -> b.getToolTipText().endsWith("Água")));
                enemy.get(0).doClick();
                enemy.get(1).doClick();
                assertFalse(fire.isEnabled());
                enemy.get(2).doClick();
                assertTrue(fire.isEnabled());
                enemy.get(3).doClick();
                assertFalse(enemy.get(3).getToolTipText().contains("selecionada"));
                enemy.get(2).doClick();
                assertFalse(fire.isEnabled());
                enemy.get(2).doClick();
                fire.doClick();
                assertFalse(fire.isEnabled());
                assertTrue(enemy.stream().noneMatch(JButton::isEnabled));
                for (int i = 0; i < 3; i++) assertFalse(enemy.get(i).getToolTipText().endsWith(": Água"));
                render(window, "board-attack-preview.png");
                window.setSize(1180, 740);
                window.validate();
                render(window, "board-small-preview.png");
                button(buttons, "Nova partida").doClick();
                assertFalse(start.isEnabled());
                assertTrue(labels(components).stream().anyMatch(s -> s.equals("0 de 11 colocados")));
                assertTrue(own.stream().allMatch(b -> b.getToolTipText().endsWith("Água")));
            } finally {
                window.dispose();
            }
        });
    }

    private static JButton button(List<JButton> buttons, String text) {
        return buttons.stream().filter(b -> b.getText().equals(text)).findFirst().orElseThrow();
    }

    private static List<String> labels(List<Component> components) {
        return components.stream().filter(JLabel.class::isInstance).map(JLabel.class::cast).map(JLabel::getText).toList();
    }

    private static void render(BoardWindow window, String filename) {
        try {
            window.validate();
            Container content = window.getContentPane();
            BufferedImage preview = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
            var graphics = preview.createGraphics();
            content.printAll(graphics);
            graphics.dispose();
            Files.createDirectories(Path.of("target"));
            ImageIO.write(preview, "png", Path.of("target", filename).toFile());
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Component> descendants(Container container) {
        List<Component> result = new ArrayList<>();
        for (Component component : container.getComponents()) {
            result.add(component);
            if (component instanceof Container child) result.addAll(descendants(child));
        }
        return result;
    }
}
