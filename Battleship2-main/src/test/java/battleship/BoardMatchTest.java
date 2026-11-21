package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

class BoardMatchTest {
    private BoardMatch started() {
        BoardMatch match = new BoardMatch(new Random(129890));
        match.randomize();
        match.start();
        return match;
    }

    @Test
    void cannotStartIncompleteFleetOrFireDuringPlacement() {
        BoardMatch match = new BoardMatch();
        assertThrows(IllegalStateException.class, match::start);
        assertThrows(IllegalStateException.class, () -> match.attack(List.of()));
    }

    @Test
    void hidesEnemyShipsUntilHitAndKeepsBoardsSeparate() {
        BoardMatch match = started();
        for (BoardView.Cell[] row : BoardView.snapshot(match.enemy(), false)) {
            for (BoardView.Cell cell : row) assertEquals(BoardView.Cell.WATER, cell);
        }
        IPosition target = match.enemy().getMyFleet().getShips().get(0).getPositions().get(0);
        List<IPosition> volley = new ArrayList<>();
        volley.add(target);
        BoardMatch.available(match.enemy()).stream().filter(p -> match.enemy().getMyFleet().shipAt(p) == null)
                .limit(2).forEach(volley::add);
        match.attack(volley);
        assertEquals(BoardView.Cell.HIT, BoardView.snapshot(match.enemy(), false)[target.getRow()][target.getColumn()]);
        assertTrue(match.player().getAlienMoves().isEmpty());
        assertEquals(BoardMatch.Phase.COMPUTER, match.phase());
        assertThrows(IllegalStateException.class, () -> match.attack(volley));
        match.computerTurn();
        assertEquals(3, BoardMatch.firedAt(match.player()).size());
        assertEquals(BoardMatch.Phase.PLAYER, match.phase());
        assertThrows(IllegalArgumentException.class, () -> match.attack(volley));
    }

    @Test
    void rejectsDuplicateAndOutsideTargets() {
        BoardMatch match = started();
        assertThrows(IllegalArgumentException.class, () -> match.attack(List.of(
                new Position(0, 0), new Position(0, 0), new Position(0, 1))));
        assertThrows(IllegalArgumentException.class, () -> match.attack(List.of(
                new Position(-1, 0), new Position(0, 1), new Position(0, 2))));
        assertTrue(match.enemy().getAlienMoves().isEmpty());
    }

    @Test
    void stopsAfterWinningWithoutAnExtraOpponentTurn() {
        BoardMatch match = started();
        List<IPosition> occupied = match.enemy().getMyFleet().getShips().stream()
                .flatMap(s -> s.getPositions().stream()).toList();
        for (int i = 0; i < occupied.size() - 1; i++) match.enemy().fireSingleShot(occupied.get(i), false);
        List<IPosition> volley = new ArrayList<>();
        volley.add(occupied.get(occupied.size() - 1));
        BoardMatch.available(match.enemy()).stream().filter(p -> match.enemy().getMyFleet().shipAt(p) == null)
                .limit(2).forEach(volley::add);
        match.attack(volley);
        assertEquals(BoardMatch.Phase.WON, match.phase());
        assertThrows(IllegalStateException.class, match::computerTurn);
    }

    @Test
    void finalVolleyPadsOnlyWhenFewerThanThreeCellsRemain() {
        Position last = new Position(9, 9);
        assertEquals(List.of(last, last, last), BoardMatch.padVolley(List.of(last)));
        assertThrows(IllegalArgumentException.class, () -> BoardMatch.padVolley(List.of()));
    }
     @Test
    @DisplayName("New match starts in placement phase")
    void testInitialState() {
        BoardMatch match = new BoardMatch();

        assertEquals(BoardMatch.Phase.PLACEMENT, match.phase());
        assertNotNull(match.player());
        assertNotNull(match.enemy());
        assertNotNull(match.placement());
        assertFalse(match.placement().complete());
    }

    @Test
    @DisplayName("Randomizing fleet allows match to start")
    void testRandomizeAndStart() {
        BoardMatch match = new BoardMatch(new Random(129880));

        match.randomize();

        assertTrue(match.placement().complete());
        assertEquals(11, match.player().getMyFleet().getShips().size());

        match.start();

        assertEquals(BoardMatch.Phase.PLAYER, match.phase());
        assertEquals(11, match.enemy().getMyFleet().getShips().size());
    }

    @Test
    @DisplayName("Placement actions are rejected after match starts")
    void testInvalidPlacementPhaseActions() {
        BoardMatch match = started();

        assertThrows(IllegalStateException.class, match::randomize);
        assertThrows(IllegalStateException.class, match::start);
        assertThrows(IllegalStateException.class,
                () -> match.loadFleet(new Fleet()));
    }

    @Test
    @DisplayName("Attack rejects incorrect number of shots")
    void testAttackInvalidShotCount() {
        BoardMatch match = started();

        assertEquals(3, match.shotsRequired());

        assertThrows(IllegalArgumentException.class,
                () -> match.attack(List.of()));

        assertThrows(IllegalArgumentException.class,
                () -> match.attack(List.of(
                        new Position(0, 0))));

        assertThrows(IllegalArgumentException.class,
                () -> match.attack(List.of(
                        new Position(0, 0),
                        new Position(0, 1),
                        new Position(0, 2),
                        new Position(0, 3))));

        assertEquals(BoardMatch.Phase.PLAYER, match.phase());
    }

    @Test
    @DisplayName("Receiving shots validates fleet and positions")
    void testReceiveShotsValidation() {
        BoardMatch match = new BoardMatch();

        assertThrows(IllegalStateException.class,
                () -> match.receiveShots(List.of(
                        new Position(0, 0),
                        new Position(0, 1),
                        new Position(0, 2))));

        match.randomize();

        assertThrows(IllegalArgumentException.class,
                () -> match.receiveShots(List.of(
                        new Position(0, 0))));

        assertThrows(IllegalArgumentException.class,
                () -> match.receiveShots(List.of(
                        new Position(-1, 0),
                        new Position(0, 1),
                        new Position(0, 2))));

        assertEquals(BoardMatch.Phase.PLACEMENT, match.phase());
    }

    @Test
    @DisplayName("Volley padding preserves existing shots")
    void testPadVolleyDifferentSizes() {
        IPosition first = new Position(0, 0);
        IPosition second = new Position(1, 1);
        IPosition third = new Position(2, 2);

        assertEquals(
                List.of(first, second, second),
                BoardMatch.padVolley(List.of(first, second)));

        assertEquals(
                List.of(first, second, third),
                BoardMatch.padVolley(List.of(first, second, third)));

        assertEquals(
                3,
                BoardMatch.padVolley(List.of(first)).size());
    }
}
