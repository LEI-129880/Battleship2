package battleship;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardViewTest {
    @Test
    void showsFleetWithoutChangingTheGame() {
        Fleet fleet = new Fleet();
        assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0))));
        Game game = new Game(fleet);
        BoardView.Cell[][] cells = BoardView.snapshot(game);
        assertEquals(BoardView.Cell.SHIP, cells[0][0]);
        assertEquals(BoardView.Cell.WATER, cells[9][9]);
        assertEquals(1, game.getRemainingShips());
        assertTrue(game.getAlienMoves().isEmpty());
    }

    @Test
    void showsHitsMissesAndSunkShips() {
        Fleet fleet = new Fleet();
        assertTrue(fleet.addShip(new Caravel(Compass.EAST, new Position(0, 0))));
        Game game = new Game(fleet);
        game.fireShots(List.of(new Position(0, 0), new Position(5, 5), new Position(9, 9)));
        BoardView.Cell[][] first = BoardView.snapshot(game);
        assertEquals(BoardView.Cell.HIT, first[0][0]);
        assertEquals(BoardView.Cell.SHIP, first[0][1]);
        assertEquals(BoardView.Cell.MISS, first[5][5]);
        game.fireShots(List.of(new Position(0, 1), new Position(6, 6), new Position(8, 8)));
        BoardView.Cell[][] last = BoardView.snapshot(game);
        assertEquals(BoardView.Cell.SUNK, last[0][0]);
        assertEquals(BoardView.Cell.SUNK, last[0][1]);
        assertEquals(0, game.getRemainingShips());
    }

    @Test
    void ignoresOutsideShotsAndPreservesRepeatedHits() {
        Fleet fleet = new Fleet();
        assertTrue(fleet.addShip(new Caravel(Compass.EAST, new Position(0, 0))));
        Game game = new Game(fleet);
        game.fireShots(List.of(new Position(0, 0), new Position(0, 0), new Position(-1, 0)));
        assertEquals(BoardView.Cell.HIT, BoardView.snapshot(game)[0][0]);
        assertEquals(1, game.getRepeatedShots());
        assertEquals(1, game.getInvalidShots());
    }
}
