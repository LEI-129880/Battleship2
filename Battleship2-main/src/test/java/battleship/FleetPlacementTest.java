package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

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
        @Test
    @DisplayName("New fleet placement starts empty and incomplete")
    void testInitialPlacementState() {
        FleetPlacement placement = new FleetPlacement();

        assertNotNull(placement.fleet());
        assertTrue(placement.fleet().getShips().isEmpty());
        assertFalse(placement.complete());

        for (FleetPlacement.Kind kind : FleetPlacement.Kind.values()) {
            assertEquals(kind.quantity, placement.remaining(kind));
        }
    }

    @Test
    @DisplayName("Undo on an empty fleet has no effect")
    void testUndoEmptyFleet() {
        FleetPlacement placement = new FleetPlacement();

        assertDoesNotThrow(placement::undo);
        assertEquals(0, placement.fleet().getShips().size());
        assertFalse(placement.complete());
    }

    @Test
    @DisplayName("Undo removes only the last placed ship")
    void testUndoLastShip() {
        FleetPlacement placement = new FleetPlacement();

        assertTrue(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 0, 0));

        assertTrue(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 0, 3));

        assertEquals(2, placement.fleet().getShips().size());

        IShip first = placement.fleet().getShips().get(0);

        placement.undo();

        assertEquals(1, placement.fleet().getShips().size());
        assertSame(first, placement.fleet().getShips().get(0));
        assertEquals(3, placement.remaining(FleetPlacement.Kind.BARGE));
    }

    @Test
    @DisplayName("Candidates preserve top-left anchor for all ship kinds")
    void testCandidateAllShipKinds() {
        FleetPlacement placement = new FleetPlacement();

        for (FleetPlacement.Kind kind : FleetPlacement.Kind.values()) {
            for (Compass bearing : Compass.values()) {
                Ship ship = placement.candidate(kind, bearing, 2, 2);

                assertNotNull(ship);
                assertEquals(kind.size, ship.getPositions().size());

                assertEquals(2, ship.getPositions().stream()
                        .mapToInt(IPosition::getRow)
                        .min().orElseThrow());

                assertEquals(2, ship.getPositions().stream()
                        .mapToInt(IPosition::getColumn)
                        .min().orElseThrow());
            }
        }
    }

    @Test
    @DisplayName("Ships cannot be placed outside board boundaries")
    void testPlacementOutsideBoard() {
        FleetPlacement placement = new FleetPlacement();

        assertFalse(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, -1, 0));

        assertFalse(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 0, -1));

        assertFalse(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 10, 0));

        assertFalse(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 0, 10));

        assertTrue(placement.fleet().getShips().isEmpty());
    }

    @Test
    @DisplayName("Loading a fleet replaces the current fleet")
    void testLoadFleet() {
        FleetPlacement placement = new FleetPlacement();

        assertTrue(placement.place(
                FleetPlacement.Kind.BARGE,
                Compass.NORTH, 0, 0));

        IFleet replacement = new Fleet();

        placement.load(replacement);

        assertSame(replacement, placement.fleet());
        assertTrue(placement.fleet().getShips().isEmpty());
        assertFalse(placement.complete());
        assertEquals(4, placement.remaining(FleetPlacement.Kind.BARGE));
    }
}
