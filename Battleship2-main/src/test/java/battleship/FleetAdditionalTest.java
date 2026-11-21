package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetAdditionalTest {

    @Test
    @DisplayName("Empty fleet has no sunk ships")
    void testEmptyFleetSunkShips() {
        Fleet fleet = new Fleet();

        assertTrue(fleet.getSunkShips().isEmpty());
    }

    @Test
    @DisplayName("Only sunk ships are returned")
    void testGetSunkShips() {
        Fleet fleet = new Fleet();

        IShip sunk = new Barge(
            Compass.NORTH, new Position(1, 1));
        IShip floating = new Barge(
            Compass.NORTH, new Position(5, 5));

        assertTrue(fleet.addShip(sunk));
        assertTrue(fleet.addShip(floating));

        sunk.sink();

        assertEquals(List.of(sunk), fleet.getSunkShips());
        assertEquals(List.of(floating), fleet.getFloatingShips());
    }

    @Test
    @DisplayName("Unknown category returns an empty list")
    void testUnknownCategory() {
        Fleet fleet = new Fleet();

        assertTrue(fleet.addShip(new Barge(
            Compass.NORTH, new Position(1, 1))));

        assertTrue(fleet.getShipsLike("Unknown").isEmpty());
    }

    @Test
    @DisplayName("Print ships handles empty and populated lists")
    void testPrintShips() {
        Fleet fleet = new Fleet();
        IShip ship = new Barge(
            Compass.NORTH, new Position(1, 1));

        assertDoesNotThrow(
            () -> fleet.printShips(List.of()));

        assertDoesNotThrow(
            () -> fleet.printShips(List.of(ship)));
    }

    @Test
    @DisplayName("Print ships by category handles both cases")
    void testPrintShipsByCategory() {
        Fleet fleet = new Fleet();

        assertTrue(fleet.addShip(new Barge(
            Compass.NORTH, new Position(1, 1))));

        assertDoesNotThrow(
            () -> fleet.printShipsByCategory("Barca"));

        assertDoesNotThrow(
            () -> fleet.printShipsByCategory("Unknown"));
    }

    @Test
    @DisplayName("Print all and floating ships handles fleet states")
    void testPrintFleetShips() {
        Fleet fleet = new Fleet();

        assertDoesNotThrow(fleet::printAllShips);
        assertDoesNotThrow(fleet::printFloatingShips);

        IShip ship = new Barge(
            Compass.NORTH, new Position(1, 1));

        assertTrue(fleet.addShip(ship));

        assertDoesNotThrow(fleet::printAllShips);
        assertDoesNotThrow(fleet::printFloatingShips);

        ship.sink();

        assertDoesNotThrow(fleet::printFloatingShips);
    }
    
    @Test
    @DisplayName("Fleet rejects ships when capacity is exceeded")
    void testFleetCapacityExceeded() {
        Fleet fleet = new Fleet();

        for (int i = 0; i <= Fleet.FLEET_SIZE; i++) {
            fleet.getShips().add(
                new Barge(
                    Compass.NORTH,
                    new Position(0, 0)
                )
            );
        }

        assertEquals(12, fleet.getShips().size());

        IShip extra = new Barge(
            Compass.NORTH,
            new Position(5, 5)
        );

        assertFalse(fleet.addShip(extra));
        assertEquals(12, fleet.getShips().size());
    }

    @Test
    @DisplayName("Fleet rejects adjacent ships without overlap")
    void testAdjacentShipsRejected() {
        Fleet fleet = new Fleet();

        IShip first = new Barge(
            Compass.NORTH,
            new Position(1, 1)
        );

        IShip adjacent = new Barge(
            Compass.NORTH,
            new Position(1, 2)
        );

        assertTrue(fleet.addShip(first));
        assertFalse(fleet.addShip(adjacent));

        assertEquals(1, fleet.getShips().size());
        assertSame(first, fleet.getShips().get(0));
    }

}
