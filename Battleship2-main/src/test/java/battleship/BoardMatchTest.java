package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
