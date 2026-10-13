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
    void selectsFiresAndResetsTheGraphicalBoard() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            FlatLightLaf.setup();
            BoardWindow window = new BoardWindow();
            try {
                List<Component> components = descendants(window.getContentPane());
                List<JButton> buttons = components.stream().filter(JButton.class::isInstance)
                        .map(JButton.class::cast).toList();
                JButton fire = buttons.stream().filter(b -> b.getText().equals("Disparar rajada")).findFirst().orElseThrow();
                JButton reset = buttons.stream().filter(b -> b.getText().equals("Nova frota")).findFirst().orElseThrow();
                List<JButton> board = buttons.stream().filter(b -> b.getToolTipText() != null).toList();
                assertEquals(100, board.size());
                assertFalse(fire.isEnabled());
                board.get(0).doClick();
                board.get(1).doClick();
                assertFalse(fire.isEnabled());
                board.get(2).doClick();
                assertTrue(fire.isEnabled());
                board.get(3).doClick();
                assertNotEquals("+", board.get(3).getText());
                board.get(2).doClick();
                assertFalse(fire.isEnabled());
                board.get(2).doClick();
                fire.doClick();
                assertFalse(fire.isEnabled());
                assertTrue(components.stream().filter(JLabel.class::isInstance).map(JLabel.class::cast)
                        .anyMatch(l -> l.getText().contains("Jogadas: 1")));
                for (int i = 0; i < 3; i++) {
                    assertTrue(List.of("O", "X", "#").contains(board.get(i).getText()));
                }
                reset.doClick();
                assertTrue(components.stream().filter(JLabel.class::isInstance).map(JLabel.class::cast)
                        .anyMatch(l -> l.getText().contains("Jogadas: 0")));
                assertTrue(board.stream().allMatch(b -> List.of("~", "N").contains(b.getText())));
                BufferedImage preview = new BufferedImage(window.getContentPane().getWidth(), window.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
                var graphics = preview.createGraphics();
                window.getContentPane().printAll(graphics);
                graphics.dispose();
                Files.createDirectories(Path.of("target"));
                ImageIO.write(preview, "png", Path.of("target/board-preview.png").toFile());
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            } finally {
                window.dispose();
            }
        });
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
