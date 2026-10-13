package battleship;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Two-board graphical client for issue #2, student 129890. */
public final class BoardWindow extends JFrame {
    private static final Color BACKGROUND = new Color(223, 224, 225);
    private static final Color PANEL = new Color(241, 242, 243);
    private static final Color INK = new Color(30, 42, 51);
    private static final Color BLUE = new Color(39, 87, 118);
    private BoardMatch match = new BoardMatch();
    private MoveClock clock;
    private int moveNumber = 1;
    private final Set<IPosition> selected = new java.util.LinkedHashSet<>();
    private FleetPlacement.Kind kind = FleetPlacement.Kind.GALLEON;
    private Compass bearing = Compass.NORTH;
    private final BoardGrid ownBoard = new BoardGrid("own");
    private final BoardGrid enemyBoard = new BoardGrid("enemy");
    private final EnumMap<FleetPlacement.Kind, JRadioButton> shipButtons =
            new EnumMap<>(FleetPlacement.Kind.class);
    private final JLabel message = new JLabel("Escolhe um navio e coloca-o no teu tabuleiro.");
    private final JLabel placementCount = new JLabel();
    private final JLabel selectionCount = new JLabel();
    private final JLabel winsLabel = new JLabel();
    private final JLabel accuracyLabel = new JLabel();
    private final JLabel ownRemaining = new JLabel();
    private final JLabel enemyRemaining = new JLabel();
    private final JLabel turnsLabel = new JLabel();
    private final JButton rotate = new JButton("Rodar: Norte");
    private final JButton undo = new JButton("Desfazer");
    private final JButton randomize = new JButton("Gerar frota");
    private final JButton start = new JButton("Começar partida");
    private final JButton fire = new JButton("Disparar rajada");
    private final Timer computer;
    private int gamesWon;
    private int completedGames;
    private boolean recorded;
    private int hoverRow = -1;
    private int hoverCol = -1;

    public BoardWindow() {
        super("Battleship2 | A tua frota e a frota inimiga");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        computer = new Timer(650, event -> {
            if (match.phase() == BoardMatch.Phase.COMPUTER) {
                match.computerTurn();
                if (match.phase() == BoardMatch.Phase.PLAYER) startClock();
                message.setText(match.phase() == BoardMatch.Phase.LOST
                        ? "A tua frota foi afundada. Podes começar uma nova partida."
                        : "O adversário respondeu. Seleciona as próximas posições na frota inimiga.");
                refresh();
            }
        });
        computer.setRepeats(false);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) { computer.stop(); closeClock(); }
        });

        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 22, 24));
        setContentPane(root);
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("BATALHA NAVAL");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setForeground(INK);
        heading.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("11 navios · Rajadas de 3 tiros");
        subtitle.setForeground(new Color(82, 92, 100));
        heading.add(subtitle, BorderLayout.EAST);
        root.add(heading, BorderLayout.NORTH);

        JPanel layout = new JPanel(new GridBagLayout());
        layout.setOpaque(false);
        JPanel boards = new JPanel(new GridLayout(1, 2, 16, 0));
        boards.setOpaque(false);
        boards.add(boardSection("A tua frota", ownBoard));
        boards.add(boardSection("Frota inimiga", enemyBoard));
        addAt(layout, alignSidebar(placementPanel()), 0, 0, 0, 1, GridBagConstraints.VERTICAL);
        addAt(layout, boards, 1, 0, 1, 1, GridBagConstraints.BOTH);
        addAt(layout, alignSidebar(statsPanel()), 2, 0, 0, 1, GridBagConstraints.VERTICAL);

        JPanel actions = new JPanel(new BorderLayout(12, 8));
        actions.setOpaque(false);
        JPanel hints = new JPanel();
        hints.setOpaque(false);
        hints.setLayout(new BoxLayout(hints, BoxLayout.Y_AXIS));
        message.setFont(message.getFont().deriveFont(13f));
        hints.add(message);
        hints.add(Box.createVerticalStrut(7));
        hints.add(selectionCount);
        actions.add(hints, BorderLayout.CENTER);
        primary(fire);
        fire.addActionListener(event -> attack());
        actions.add(fire, BorderLayout.EAST);
        GridBagConstraints below = new GridBagConstraints();
        below.gridx = 1; below.gridy = 1;
        below.weightx = 1; below.fill = GridBagConstraints.HORIZONTAL;
        below.insets = new Insets(18, 8, 0, 8);
        layout.add(actions, below);
        root.add(layout, BorderLayout.CENTER);

        JLabel legend = new JLabel("Cinza: navio     × Vermelho: acerto     ○ Azul escuro: água     ⊕ Amarelo: seleção",
                SwingConstants.CENTER);
        legend.setForeground(new Color(72, 83, 91));
        legend.setFont(legend.getFont().deriveFont(12f));
        root.add(legend, BorderLayout.SOUTH);
        ownBoard.onClick(this::place);
        ownBoard.onHover(this::preview, () -> {
            hoverRow = -1; hoverCol = -1; ownBoard.clearPreview();
        });
        enemyBoard.onClick(this::selectShot);
        refresh();
        pack();
        setSize(1380, 760);
        setMinimumSize(new Dimension(1180, 740));
        setLocationRelativeTo(null);
    }

    private JPanel placementPanel() {
        JPanel panel = sidePanel();
        JLabel heading = new JLabel("<html>Coloca a<br>tua frota</html>");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 21f));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(12));
        panel.add(placementCount);
        panel.add(Box.createVerticalStrut(18));
        ButtonGroup group = new ButtonGroup();
        for (FleetPlacement.Kind entry : FleetPlacement.Kind.values()) {
            JRadioButton button = new JRadioButton();
            button.setOpaque(false);
            button.setName("ship-" + entry.key);
            button.setFont(button.getFont().deriveFont(14f));
            button.setMaximumSize(new Dimension(170, 32));
            button.addActionListener(event -> { kind = entry; updatePreview(); });
            group.add(button);
            panel.add(button);
            shipButtons.put(entry, button);
        }
        panel.add(Box.createVerticalStrut(16));
        rotate.addActionListener(event -> {
            bearing = switch (bearing) {
                case NORTH -> Compass.EAST;
                case EAST -> Compass.SOUTH;
                case SOUTH -> Compass.WEST;
                case WEST -> Compass.NORTH;
            };
            refresh();
            updatePreview();
        });
        undo.addActionListener(event -> {
            match.placement().undo();
            closeClock();
            message.setText("Último navio retirado. Escolhe onde o colocar.");
            refresh();
        });
        randomize.addActionListener(event -> {
            match.randomize();
            moveNumber = 1;
            startClock();
            message.setText("Frota pronta. Clica em Começar partida.");
            refresh();
        });
        start.addActionListener(event -> {
            match.start();
            startClock();
            message.setText("A frota inimiga está escondida. Seleciona três posições para atacar.");
            refresh();
        });
        for (JButton button : List.of(rotate, undo, randomize, start)) {
            button.setAlignmentX(LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(170, 38));
            panel.add(button);
            panel.add(Box.createVerticalStrut(8));
        }
        primary(start);
        panel.add(Box.createVerticalGlue());
        JLabel rules = new JLabel("<html><body style='width:120px'>Os navios não podem tocar-se, nem na diagonal.</body></html>");
        rules.setForeground(new Color(91, 98, 102));
        rules.setFont(rules.getFont().deriveFont(12f));
        panel.add(rules);
        return panel;
    }

    private JPanel statsPanel() {
        JPanel panel = sidePanel();
        JLabel title = new JLabel("Estatísticas");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        metric(panel, "Partidas ganhas", winsLabel);
        metric(panel, "Precisão", accuracyLabel);
        metric(panel, "Os teus navios", ownRemaining);
        metric(panel, "Navios inimigos", enemyRemaining);
        metric(panel, "Rajadas disparadas", turnsLabel);
        panel.add(Box.createVerticalGlue());
        JButton reset = new JButton("Nova partida");
        reset.setName("new-match");
        reset.setAlignmentX(LEFT_ALIGNMENT);
        reset.setMaximumSize(new Dimension(170, 38));
        primary(reset);
        reset.addActionListener(event -> resetMatch());
        panel.add(reset);
        panel.add(Box.createVerticalStrut(12));
        JLabel session = new JLabel("Resultados desta sessão");
        session.setFont(session.getFont().deriveFont(11f));
        session.setForeground(new Color(91, 98, 102));
        panel.add(session);
        return panel;
    }

    private void place(int row, int col) {
        if (match.phase() != BoardMatch.Phase.PLACEMENT || match.placement().complete()) return;
        if (match.placement().place(kind, bearing, row, col)) {
            hoverRow = -1;
            hoverCol = -1;
            ownBoard.clearPreview();
            if (match.placement().complete()) startClock();
            message.setText(match.placement().complete()
                    ? "Os 11 navios estão colocados. Podes começar a partida."
                    : kind.label + " inteiro colocado (" + kind.size + " casas). Escolhe o próximo navio.");
        } else {
            message.setText("Posição inválida: mantém o navio dentro do tabuleiro e afastado dos restantes.");
        }
        refresh();
    }

    private void preview(int row, int col) {
        hoverRow = row; hoverCol = col;
        updatePreview();
    }

    private void updatePreview() {
        if (match.phase() != BoardMatch.Phase.PLACEMENT || match.placement().complete() || hoverRow < 0) return;
        Ship candidate = match.placement().candidate(kind, bearing, hoverRow, hoverCol);
        ownBoard.preview(candidate.getPositions(), match.placement().canPlace(kind, candidate));
    }

    private void selectShot(int row, int col) {
        if (match.phase() != BoardMatch.Phase.PLAYER) return;
        Position position = new Position(row, col);
        if (BoardMatch.firedAt(match.enemy()).contains(position)) return;
        if (selected.contains(position)) selected.remove(position);
        else if (selected.size() < match.shotsRequired()) selected.add(position);
        refresh();
    }

    private void attack() {
        if (match.phase() != BoardMatch.Phase.PLAYER || selected.size() != match.shotsRequired()) return;
        match.attack(new ArrayList<>(selected));
        finishTurn();
        selected.clear();
        message.setText(match.phase() == BoardMatch.Phase.WON
                ? "Vitória! Afundaste toda a frota inimiga."
                : "Rajada disparada. O adversário está a responder…");
        refresh();
        if (match.phase() == BoardMatch.Phase.COMPUTER) computer.start();
    }

    private void resetMatch() {
        computer.stop();
        closeClock();
        moveNumber = 1;
        match = new BoardMatch();
        selected.clear();
        kind = FleetPlacement.Kind.GALLEON;
        bearing = Compass.NORTH;
        hoverRow = -1; hoverCol = -1;
        recorded = false;
        message.setText("Escolhe um navio e coloca-o no teu tabuleiro.");
        refresh();
    }

    private void startClock() {
        if (clock == null) clock = new MoveClock();
        clock.start(moveNumber);
    }

    private void finishTurn() {
        if (clock != null) System.out.println("Tempo da jogada " + moveNumber + ": " + clock.stop());
        moveNumber++;
    }

    private void closeClock() {
        if (clock != null) { clock.close(); clock = null; }
    }

    // Console commands and button callbacks run on the same Swing event thread.
    Game consoleGame() { return match.player(); }

    void consoleFleet(IFleet fleet) {
        resetMatch();
        match.loadFleet(fleet);
        startClock();
        message.setText("Frota carregada da consola. Podes começar a partida.");
        refresh();
    }

    void consoleShots(List<IPosition> shots) {
        boolean answeringPlayer = match.phase() == BoardMatch.Phase.COMPUTER;
        match.receiveShots(shots);
        computer.stop();
        selected.clear();
        if (!answeringPlayer) finishTurn();
        if (match.phase() == BoardMatch.Phase.PLAYER) startClock();
        message.setText(match.phase() == BoardMatch.Phase.LOST
                ? "A tua frota foi afundada. Gera uma nova frota."
                : "Rajada da consola aplicada à tua frota.");
        refresh();
    }

    void consoleRandomShots() {
        List<IPosition> available = BoardMatch.available(match.player());
        java.util.Collections.shuffle(available);
        consoleShots(BoardMatch.padVolley(available.subList(0, Math.min(Game.NUMBER_SHOTS, available.size()))));
    }

    private void refresh() {
        boolean placing = match.phase() == BoardMatch.Phase.PLACEMENT;
        boolean playing = match.phase() == BoardMatch.Phase.PLAYER;
        boolean finished = match.phase() == BoardMatch.Phase.WON || match.phase() == BoardMatch.Phase.LOST;
        if (finished && !recorded) {
            recorded = true;
            completedGames++;
            if (match.phase() == BoardMatch.Phase.WON) gamesWon++;
        }
        if (match.placement().remaining(kind) == 0) {
            for (FleetPlacement.Kind available : FleetPlacement.Kind.values()) {
                if (match.placement().remaining(available) > 0) { kind = available; break; }
            }
        }
        for (FleetPlacement.Kind entry : FleetPlacement.Kind.values()) {
            JRadioButton button = shipButtons.get(entry);
            int remaining = match.placement().remaining(entry);
            button.setText(entry.label + " (" + entry.size + ") × " + remaining);
            button.setToolTipText(entry.size + " posições" + (entry == FleetPlacement.Kind.GALLEON ? ", em T" : ""));
            button.setSelected(entry == kind);
            button.setEnabled(placing && remaining > 0);
        }
        placementCount.setText(match.placement().fleet().getShips().size() + " de 11 colocados");
        rotate.setText("Rodar: " + switch (bearing) {
            case NORTH -> "Norte"; case SOUTH -> "Sul"; case EAST -> "Este"; case WEST -> "Oeste";
        });
        rotate.setEnabled(placing && !match.placement().complete());
        undo.setEnabled(placing && !match.placement().fleet().getShips().isEmpty());
        randomize.setEnabled(placing);
        start.setEnabled(placing && match.placement().complete());
        fire.setEnabled(playing && selected.size() == match.shotsRequired());
        ownBoard.display(BoardView.snapshot(match.player()), Set.of(), Set.of(),
                placing && !match.placement().complete());
        enemyBoard.display(BoardView.snapshot(match.enemy(), finished), selected,
                BoardMatch.firedAt(match.enemy()), playing);
        selectionCount.setText(placing ? "1 clique coloca o navio inteiro. Verde: cabe; contorno vermelho: posição inválida."
                : finished ? "Partida terminada. Usa Nova partida para voltar a jogar."
                : match.phase() == BoardMatch.Phase.COMPUTER ? "Turno do adversário"
                : "Rajada: " + selected.size() + " de " + match.shotsRequired() + " posições selecionadas");
        winsLabel.setText(gamesWon + " de " + completedGames);
        int shots = BoardMatch.firedAt(match.enemy()).size();
        accuracyLabel.setText(shots == 0 ? "0%" : String.format(Locale.ROOT, "%.0f%%", 100.0 * match.enemy().getHits() / shots));
        ownRemaining.setText(match.player().getRemainingShips() + " a flutuar");
        enemyRemaining.setText(placing ? "—" : match.enemy().getRemainingShips() + " a flutuar");
        turnsLabel.setText(String.valueOf(match.enemy().getAlienMoves().size()));
        getContentPane().revalidate();
    }

    private JPanel sidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        panel.setPreferredSize(new Dimension(190, 455));
        return panel;
    }

    private JPanel boardSection(String heading, BoardGrid grid) {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setOpaque(false);
        section.add(title(heading), BorderLayout.NORTH);
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel alignSidebar(JPanel sidebar) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(64, 0, 0, 0));
        wrapper.add(sidebar, BorderLayout.CENTER);
        return wrapper;
    }

    private void metric(JPanel panel, String label, JLabel value) {
        JLabel caption = new JLabel(label);
        caption.setFont(caption.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(caption);
        panel.add(Box.createVerticalStrut(5));
        value.setForeground(BLUE);
        value.setFont(value.getFont().deriveFont(16f));
        panel.add(value);
        panel.add(Box.createVerticalStrut(18));
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 23f));
        label.setForeground(INK);
        return label;
    }

    private void primary(JButton button) {
        button.setBackground(BLUE);
        button.setForeground(new Color(246, 248, 249));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setMargin(new Insets(10, 12, 10, 12));
    }

    private void addAt(JPanel panel, java.awt.Component item, int x, int y,
                       double weightX, double weightY, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y; c.weightx = weightX; c.weighty = weightY;
        c.fill = fill; c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(y == 0 ? 0 : 14, 6, 0, 6);
        panel.add(item, c);
    }

    /** Opens the graphical client. The console client remains available in Main. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            javax.swing.UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            new BoardWindow().setVisible(true);
        });
    }
}
