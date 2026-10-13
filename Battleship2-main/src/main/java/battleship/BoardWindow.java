package battleship;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** Graphical simulation board for issue #2 (student 129890). */
public final class BoardWindow extends JFrame {
    private IGame game;
    private final List<IPosition> selected = new ArrayList<>();
    private final JButton[][] cells = new JButton[Game.BOARD_SIZE][Game.BOARD_SIZE];
    private final JLabel status = new JLabel();
    private final JLabel instruction = new JLabel();
    private final JButton fire = new JButton("Disparar rajada");

    public BoardWindow() {
        super("Battleship2 | Tabuleiro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(content);

        JPanel heading = new JPanel(new GridLayout(0, 1, 0, 6));
        JLabel title = new JLabel("Batalha Naval");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        heading.add(title);
        heading.add(new JLabel("Simulação: a frota está visível. Seleciona três posições e dispara."));
        heading.add(status);
        content.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(11, 11, 3, 3));
        grid.setPreferredSize(new Dimension(550, 550));
        grid.add(new JLabel());
        for (int col = 1; col <= Game.BOARD_SIZE; col++) {
            grid.add(new JLabel(String.valueOf(col), SwingConstants.CENTER));
        }
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            grid.add(new JLabel(String.valueOf((char) ('A' + row)), SwingConstants.CENTER));
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                final int r = row, c = col;
                JButton cell = new JButton();
                cell.setFont(cell.getFont().deriveFont(Font.BOLD, 17f));
                cell.setMargin(new java.awt.Insets(0, 0, 0, 0));
                cell.addActionListener(event -> toggle(r, c));
                cells[row][col] = cell;
                grid.add(cell);
            }
        }
        content.add(grid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 10));
        footer.add(new JLabel("~ Água     N Navio     O Falhou     X Atingido     # Afundado"));
        footer.add(instruction);
        JPanel actions = new JPanel(new BorderLayout(12, 0));
        JButton newFleet = new JButton("Nova frota");
        newFleet.addActionListener(event -> startGame());
        fire.addActionListener(event -> fireSelected());
        actions.add(newFleet, BorderLayout.WEST);
        actions.add(fire, BorderLayout.EAST);
        footer.add(actions);
        content.add(footer, BorderLayout.SOUTH);
        startGame();
        pack();
        setMinimumSize(new Dimension(620, 720));
        setLocationRelativeTo(null);
    }

    private void startGame() {
        game = new Game(Fleet.createRandom());
        selected.clear();
        refresh();
    }

    private void toggle(int row, int col) {
        IPosition position = new Position(row, col);
        if (selected.contains(position)) selected.remove(position);
        else if (selected.size() < Game.NUMBER_SHOTS) selected.add(position);
        refresh();
    }

    private void fireSelected() {
        if (selected.size() != Game.NUMBER_SHOTS || game.getRemainingShips() == 0) return;
        game.fireShots(List.copyOf(selected));
        selected.clear();
        refresh();
    }

    private void refresh() {
        BoardView.Cell[][] snapshot = BoardView.snapshot(game);
        boolean finished = game.getRemainingShips() == 0;
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                BoardView.Cell state = snapshot[row][col];
                boolean pending = selected.contains(new Position(row, col));
                JButton cell = cells[row][col];
                String coordinate = String.valueOf((char) ('A' + row)) + (col + 1);
                cell.setText(pending ? "+" : state.symbol);
                cell.setBackground(pending ? new Color(248, 213, 110) : color(state));
                cell.setForeground(new Color(29, 41, 48));
                String label = coordinate + ": " + state.description + (pending ? ", selecionada" : "");
                cell.setToolTipText(label);
                cell.getAccessibleContext().setAccessibleName(label);
                // Repeated shots remain possible, including the final three-shot volley.
                cell.setEnabled(!finished);
            }
        }
        status.setText("Navios a flutuar: " + game.getRemainingShips()
                + "    Afundados: " + game.getSunkShips() + "    Jogadas: " + game.getAlienMoves().size());
        instruction.setText(finished ? "Todos os navios afundados. Podes iniciar uma nova frota."
                : "Posições selecionadas: " + selected.size() + "/3. Clica novamente para retirar uma seleção.");
        fire.setEnabled(!finished && selected.size() == Game.NUMBER_SHOTS);
    }

    private static Color color(BoardView.Cell cell) {
        return switch (cell) {
            case WATER -> new Color(225, 238, 244);
            case SHIP -> new Color(160, 189, 174);
            case MISS -> new Color(211, 219, 223);
            case HIT -> new Color(245, 193, 145);
            case SUNK -> new Color(224, 158, 153);
        };
    }

    /** Opens the graphical client; the original console client remains in Main. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new BoardWindow().setVisible(true);
        });
    }
}
