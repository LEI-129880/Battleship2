
package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.commons.lang3.time.StopWatch;

/** Relogio da jogada, apresentado numa janela independente da consola. */
public final class MoveClock {
    private final StopWatch watch = new StopWatch();
    private JFrame frame;
    private JLabel display;
    private JLabel heading;
    private Timer refresh;
    private volatile int move = 1;
    private volatile boolean closed;

    public MoveClock() {
        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(this::createWindow);
        }
    }

    private void createWindow() {
        if (closed) return;
        frame = new JFrame("Battleship | Relógio");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(310, 175);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(19, 31, 51));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 20, 20));

        heading = new JLabel("JOGADA 1", SwingConstants.CENTER);
        heading.setForeground(new Color(151, 190, 228));
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));

        display = new JLabel("00:00.0", SwingConstants.CENTER);
        display.setForeground(Color.WHITE);
        display.setFont(new Font("Monospaced", Font.BOLD, 42));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(display, BorderLayout.CENTER);
        frame.setContentPane(panel);

        refresh = new Timer(100, event -> updateDisplay());
        refresh.start();
        updateDisplay();
    }

    private synchronized String elapsed() {
        long millis = watch.getTime(TimeUnit.MILLISECONDS);
        return String.format("%02d:%02d.%01d",
                millis / 60000,
                (millis / 1000) % 60,
                (millis % 1000) / 100);
    }

    private void updateDisplay() {
        if (display != null) {
            display.setText(elapsed());
            heading.setText("JOGADA " + move);
        }
    }

    /** Inicia um jogo novo ou uma jogada seguinte, com o tempo a zero. */
    public synchronized void start(int number) {
        if (closed) return;
        move = number;
        watch.reset();
        watch.start();

        SwingUtilities.invokeLater(() -> {
            if (!closed && frame != null) {
                frame.setVisible(true);
                updateDisplay();
            }
        });
    }

    /** Para o relogio e devolve o tempo final para mostrar na consola. */
    public synchronized String stop() {
        if (watch.isStarted() && !watch.isStopped()) {
            watch.stop();
        }

        String result = elapsed();
        SwingUtilities.invokeLater(this::updateDisplay);
        return result;
    }

    public synchronized void close() {
        closed = true;
        if (watch.isStarted() && !watch.isStopped()) watch.stop();
        SwingUtilities.invokeLater(() -> {
            if (refresh != null) refresh.stop();
            if (frame != null) frame.dispose();
        });
    }
}
