package battleship;

/** Read-only representation of the current fleet and recorded shots for the GUI. */
final class BoardView {
    enum Cell {
        WATER("~", "Água"), SHIP("N", "Navio"),
        MISS("O", "Tiro na água"), HIT("X", "Navio atingido"), SUNK("#", "Navio afundado");

        final String symbol;
        final String description;

        Cell(String symbol, String description) {
            this.symbol = symbol;
            this.description = description;
        }
    }

    private BoardView() {}

    static Cell[][] snapshot(IGame game) {
        return snapshot(game, true);
    }

    /** Hides unhit enemy ships without changing the underlying game. */
    static Cell[][] snapshot(IGame game, boolean showShips) {
        Cell[][] cells = new Cell[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                IPosition position = new Position(row, col);
                IShip ship = game.getMyFleet().shipAt(position);
                cells[row][col] = ship == null || !showShips ? Cell.WATER
                        : ship.stillFloating() ? Cell.SHIP : Cell.SUNK;
            }
        }
        for (IMove move : game.getAlienMoves()) {
            for (IPosition shot : move.getShots()) {
                if (!shot.isInside()) continue;
                int row = shot.getRow();
                int col = shot.getColumn();
                IShip ship = game.getMyFleet().shipAt(shot);
                cells[row][col] = ship == null ? Cell.MISS
                        : ship.stillFloating() ? Cell.HIT : Cell.SUNK;
            }
        }
        return cells;
    }
}
