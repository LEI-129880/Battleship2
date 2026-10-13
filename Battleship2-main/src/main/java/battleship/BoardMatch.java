package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Two defending boards sharing the existing three-shot Game rules. */
final class BoardMatch {
    enum Phase { PLACEMENT, PLAYER, COMPUTER, WON, LOST }

    private final FleetPlacement placement = new FleetPlacement();
    private final Random random;
    private Game player = new Game(placement.fleet());
    private Game enemy = new Game(new Fleet());
    private Phase phase = Phase.PLACEMENT;

    BoardMatch() { this(new Random()); }
    BoardMatch(Random random) { this.random = random; }

    FleetPlacement placement() { return placement; }
    Game player() { return player; }
    Game enemy() { return enemy; }
    Phase phase() { return phase; }

    void randomize() {
        require(Phase.PLACEMENT);
        placement.randomize();
        player = new Game(placement.fleet());
    }

    void start() {
        require(Phase.PLACEMENT);
        if (!placement.complete()) throw new IllegalStateException("Coloca os 11 navios antes de começar.");
        enemy = new Game(Fleet.createRandom());
        phase = Phase.PLAYER;
    }

    int shotsRequired() { return Math.min(Game.NUMBER_SHOTS, available(enemy).size()); }

    void attack(List<IPosition> shots) {
        require(Phase.PLAYER);
        Set<IPosition> available = new HashSet<>(available(enemy));
        if (shots.size() != shotsRequired() || new HashSet<>(shots).size() != shots.size()
                || !available.containsAll(shots)) {
            throw new IllegalArgumentException("Seleciona posições diferentes que ainda não foram atingidas.");
        }
        enemy.fireShots(padVolley(shots));
        phase = enemy.getRemainingShips() == 0 ? Phase.WON : Phase.COMPUTER;
    }

    void computerTurn() {
        require(Phase.COMPUTER);
        // The opponent chooses only from coordinates not fired on, without inspecting ships.
        List<IPosition> candidates = available(player);
        Collections.shuffle(candidates, random);
        player.fireShots(padVolley(candidates.subList(0, Math.min(Game.NUMBER_SHOTS, candidates.size()))));
        phase = player.getRemainingShips() == 0 ? Phase.LOST : Phase.PLAYER;
    }

    static Set<IPosition> firedAt(IGame game) {
        Set<IPosition> shots = new HashSet<>();
        for (IMove move : game.getAlienMoves()) {
            for (IPosition position : move.getShots()) if (position.isInside()) shots.add(position);
        }
        return shots;
    }

    static List<IPosition> available(IGame game) {
        Set<IPosition> fired = firedAt(game);
        List<IPosition> result = new ArrayList<>();
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                Position position = new Position(row, col);
                if (!fired.contains(position)) result.add(position);
            }
        }
        return result;
    }

    static List<IPosition> padVolley(List<IPosition> shots) {
        if (shots.isEmpty()) throw new IllegalArgumentException("A rajada não pode estar vazia.");
        List<IPosition> volley = new ArrayList<>(shots);
        // Preserve the engine's three-shot contract when fewer than three cells remain.
        while (volley.size() < Game.NUMBER_SHOTS) volley.add(shots.get(shots.size() - 1));
        return volley;
    }

    private void require(Phase expected) {
        if (phase != expected) throw new IllegalStateException("Ação indisponível nesta fase da partida.");
    }
}
