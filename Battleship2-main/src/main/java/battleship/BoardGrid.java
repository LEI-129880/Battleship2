package battleship;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/** Blue square grid, with block ships and hit marks rather than text-filled buttons. */
final class BoardGrid extends JPanel {
    static final Color SEA = new Color(155, 195, 222);
    static final Color LINE = new Color(43, 87, 113);
    private final CellButton[][] cells = new CellButton[10][10];
    private final JPanel columns = new JPanel(new GridLayout(1, 10));
    private final JPanel rows = new JPanel(new GridLayout(10, 1));
    private final JPanel grid = new JPanel(new GridLayout(10, 10, 1, 1));
    private BiConsumer<Integer, Integer> onClick = (r, c) -> {};
    private BiConsumer<Integer, Integer> onHover = (r, c) -> {};
    private Runnable onLeave = () -> {};

    BoardGrid(String name) {
        super(null);
        setOpaque(false);
        columns.setOpaque(false);
        for (int col = 1; col <= 10; col++) columns.add(coordinate(String.valueOf(col)));
        columns.setPreferredSize(new Dimension(430, 24));
        add(columns, BorderLayout.NORTH);
        rows.setOpaque(false);
        rows.setPreferredSize(new Dimension(28, 400));
        for (int row = 0; row < 10; row++) rows.add(coordinate(String.valueOf((char) ('A' + row))));
        add(rows, BorderLayout.WEST);
        grid.setBackground(LINE);
        grid.setBorder(BorderFactory.createLineBorder(LINE, 4));
        grid.setPreferredSize(new Dimension(420, 420));
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row, c = col;
                CellButton cell = new CellButton();
                cell.setName(name + "-" + (char) ('A' + row) + (col + 1));
                cell.addActionListener(e -> onClick.accept(r, c));
                cell.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent event) { onHover.accept(r, c); }
                    @Override public void mouseExited(MouseEvent event) { onLeave.run(); }
                });
                bindArrow(cell, "LEFT", r, Math.max(0, c - 1));
                bindArrow(cell, "RIGHT", r, Math.min(9, c + 1));
                bindArrow(cell, "UP", Math.max(0, r - 1), c);
                bindArrow(cell, "DOWN", Math.min(9, r + 1), c);
                cells[row][col] = cell;
                grid.add(cell);
            }
        }
        add(grid, BorderLayout.CENTER);
        setPreferredSize(new Dimension(448, 444));
    }

    @Override public void doLayout() {
        int side = Math.max(10, Math.min(getWidth() - 28, getHeight() - 24));
        int left = 28 + Math.max(0, (getWidth() - 28 - side) / 2);
        columns.setBounds(left + 4, 0, side - 8, 24);
        rows.setBounds(left - 28, 28, 28, side - 8);
        grid.setBounds(left, 24, side, side);
    }

    void onClick(BiConsumer<Integer, Integer> action) { onClick = action; }
    void onHover(BiConsumer<Integer, Integer> action, Runnable leave) { onHover = action; onLeave = leave; }

    void display(BoardView.Cell[][] states, Set<IPosition> selected, Set<IPosition> unavailable, boolean interactive) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellButton button = cells[row][col];
                Position position = new Position(row, col);
                button.state = states[row][col];
                button.selected = selected.contains(position);
                button.preview = 0;
                button.setEnabled(interactive && !unavailable.contains(position));
                String label = String.valueOf((char) ('A' + row)) + (col + 1) + ": "
                        + button.state.description + (button.selected ? ", selecionada" : "");
                button.setToolTipText(label);
                button.getAccessibleContext().setAccessibleName(label);
                button.repaint();
            }
        }
    }

    void preview(List<IPosition> positions, boolean valid) {
        clearPreview();
        for (IPosition position : positions) {
            if (!position.isInside()) continue;
            CellButton button = cells[position.getRow()][position.getColumn()];
            button.preview = valid ? 1 : -1;
            button.repaint();
        }
    }

    void clearPreview() {
        for (CellButton[] row : cells) for (CellButton button : row) {
            if (button.preview != 0) { button.preview = 0; button.repaint(); }
        }
    }

    private JLabel coordinate(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(new Color(75, 90, 100));
        label.setFont(label.getFont().deriveFont(11f));
        return label;
    }

    private void bindArrow(JButton button, String key, int row, int col) {
        button.getInputMap().put(KeyStroke.getKeyStroke(key), key);
        button.getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                cells[row][col].requestFocusInWindow();
                onHover.accept(row, col);
            }
        });
    }

    private static final class CellButton extends JButton {
        private BoardView.Cell state = BoardView.Cell.WATER;
        private boolean selected;
        private int preview;

        CellButton() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = switch (state) {
                case SHIP -> new Color(133, 137, 141);
                case MISS -> new Color(108, 160, 195);
                case HIT -> new Color(229, 68, 73);
                case SUNK -> new Color(188, 49, 56);
                default -> SEA;
            };
            if (selected) color = new Color(235, 200, 105);
            if (preview > 0) color = new Color(149, 185, 161);
            else if (isEnabled() && getModel().isRollover() && !selected) color = color.brighter();
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
            if (preview != 0) {
                g.setColor(preview > 0 ? new Color(42, 115, 65) : new Color(190, 45, 45));
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[] {4f, 3f}, 0f));
                g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
            }
            int size = Math.min(getWidth(), getHeight());
            int cx = getWidth() / 2, cy = getHeight() / 2;
            g.setColor(new Color(29, 44, 54));
            g.setStroke(new BasicStroke(2f));
            if (state == BoardView.Cell.HIT || state == BoardView.Cell.SUNK) {
                int half = Math.max(5, size / 5);
                g.drawLine(cx - half, cy - half, cx + half, cy + half);
                g.drawLine(cx - half, cy + half, cx + half, cy - half);
            } else if (state == BoardView.Cell.MISS) {
                int diameter = Math.max(5, size / 6);
                g.drawOval(cx - diameter / 2, cy - diameter / 2, diameter, diameter);
            }
            if (selected) {
                g.drawOval(cx - 7, cy - 7, 14, 14);
                g.drawLine(cx - 11, cy, cx + 11, cy);
                g.drawLine(cx, cy - 11, cx, cy + 11);
            }
            if (hasFocus()) {
                g.setColor(LINE);
                g.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
            }
            g.dispose();
        }
    }
}
