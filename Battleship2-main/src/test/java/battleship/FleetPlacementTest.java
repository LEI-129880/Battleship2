package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FleetPlacementTest {
    @Test
    void rotationsUseTopLeftAnchorAndKeepGalleonShape() {
        FleetPlacement placement = new FleetPlacement();
        for (Compass bearing : Compass.values()) {
            Ship ship = placement.candidate(FleetPlacement.Kind.GALLEON, bearing, 0, 0);
            assertEquals(5, ship.getPositions().size());
            assertTrue(ship.isInsideBoard());
            assertEquals(0, ship.getPositions().stream().mapToInt(IPosition::getRow).min().orElseThrow());
            assertEquals(0, ship.getPositions().stream().mapToInt(IPosition::getColumn).min().orElseThrow());
        }
    }

    @Test
    void preventsCollisionsAdjacencyBoundsAndTooManyShips() {
        FleetPlacement placement = new FleetPlacement();
        assertTrue(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 0));
        assertFalse(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 0));
        assertFalse(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 1, 1));
        assertFalse(placement.place(FleetPlacement.Kind.GALLEON, Compass.SOUTH, 9, 9));
        assertTrue(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 2));
        assertTrue(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 4));
        assertTrue(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 6));
        assertFalse(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 8));
        placement.undo();
        assertEquals(1, placement.remaining(FleetPlacement.Kind.BARGE));
        assertTrue(placement.place(FleetPlacement.Kind.BARGE, Compass.NORTH, 0, 8));
    }

    @Test
    void generatedFleetHasExactlyTheAssignmentInventory() {
        FleetPlacement placement = new FleetPlacement();
        placement.randomize();
        assertTrue(placement.complete());
        for (FleetPlacement.Kind kind : FleetPlacement.Kind.values()) assertEquals(0, placement.remaining(kind));
        assertEquals(25, placement.fleet().getShips().stream().mapToInt(s -> s.getPositions().size()).sum());
    }
}
