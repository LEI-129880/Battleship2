package battleship;

import java.util.List;

/** Places the eleven ships from the assignment using the original collision rules. */
final class FleetPlacement {
    enum Kind {
        GALLEON("galeao", "Galeão", "Galeao", 1, 5),
        FRIGATE("fragata", "Fragata", "Fragata", 1, 4),
        CARRACK("nau", "Nau", "Nau", 2, 3),
        CARAVEL("caravela", "Caravela", "Caravela", 3, 2),
        BARGE("barca", "Barca", "Barca", 4, 1);

        final String key, label, category;
        final int quantity, size;

        Kind(String key, String label, String category, int quantity, int size) {
            this.key = key;
            this.label = label;
            this.category = category;
            this.quantity = quantity;
            this.size = size;
        }
    }

    private IFleet fleet = new Fleet();

    IFleet fleet() { return fleet; }

    int remaining(Kind kind) {
        return kind.quantity - fleet.getShipsLike(kind.category).size();
    }

    boolean complete() { return fleet.getShips().size() == IFleet.FLEET_SIZE; }

    /** The pointer always refers to the top-left of the rotated bounding box. */
    Ship candidate(Kind kind, Compass bearing, int row, int col) {
        Ship shape = Ship.buildShip(kind.key, bearing, new Position(4, 4));
        int minRow = shape.getPositions().stream().mapToInt(IPosition::getRow).min().orElseThrow();
        int minCol = shape.getPositions().stream().mapToInt(IPosition::getColumn).min().orElseThrow();
        return Ship.buildShip(kind.key, bearing, new Position(row + 4 - minRow, col + 4 - minCol));
    }

    boolean canPlace(Kind kind, Ship ship) {
        return remaining(kind) > 0 && ship.isInsideBoard()
                && fleet.getShips().stream().noneMatch(existing -> existing.tooCloseTo(ship));
    }

    boolean place(Kind kind, Compass bearing, int row, int col) {
        Ship ship = candidate(kind, bearing, row, col);
        return canPlace(kind, ship) && fleet.addShip(ship);
    }

    void undo() {
        List<IShip> ships = fleet.getShips();
        if (!ships.isEmpty()) ships.remove(ships.size() - 1);
    }

    void randomize() { fleet = Fleet.createRandom(); }

    void load(IFleet fleet) { this.fleet = fleet; }
}
