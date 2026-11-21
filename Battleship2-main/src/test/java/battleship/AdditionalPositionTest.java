package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Testes adicionais de Position (129890), criados na ficha 4 para maximizar a cobertura.
 * Os testes originais continuam em PositionTest.
 */
@DisplayName("Testes adicionais da classe Position")
public class AdditionalPositionTest {

	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	}

	@Test
	@DisplayName("O construtor clássico converte letra e número para linha e coluna")
	void constructorClassic() {
		Position pos = new Position('C', 4);
		assertEquals(2, pos.getRow(), "Row 'C' should be converted to 2");
		assertEquals(3, pos.getColumn(), "Column 4 should be converted to 3");
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
		assertEquals(position, pos, "Position('C', 4) should be equal to Position(2, 3)");
	}

	@Test
	@DisplayName("O construtor clássico aceita letras minúsculas")
	void constructorClassicLowerCase() {
		Position pos = new Position('c', 4);
		assertEquals(2, pos.getRow(), "Row 'c' should be converted to 2");
		assertEquals(3, pos.getColumn(), "Column 4 should be converted to 3");
	}

	@Test
	@DisplayName("randomPosition devolve sempre uma posição dentro do tabuleiro")
	void randomPosition() {
		for (int i = 0; i < 200; i++) {
			Position random = Position.randomPosition();
			assertNotNull(random, "randomPosition should never return null");
			assertTrue(random.isInside(), "randomPosition returned a position outside the board: " + random);
		}
	}

	@Test
	@DisplayName("isInside: o canto inferior direito ainda está dentro do tabuleiro")
	void isValid6() {
		position = new Position(Game.BOARD_SIZE - 1, Game.BOARD_SIZE - 1);
		assertTrue(position.isInside(), "Position (BOARD_SIZE-1, BOARD_SIZE-1) should be valid");
	}

	@Test
	@DisplayName("isAdjacentTo: mesma linha mas coluna a mais de 1 de distância não é vizinha")
	void isAdjacentTo5() {
		Position other = new Position(2, 5);
		assertFalse(position.isAdjacentTo(other), "Position two columns away should not be adjacent");
	}

	@Test
	@DisplayName("isAdjacentTo: a própria posição conta como adjacente")
	void isAdjacentToItself() {
		assertTrue(position.isAdjacentTo(new Position(2, 3)), "A position is at distance 0 of itself");
	}

	@Test
	@DisplayName("adjacentPositions: uma posição no meio tem 8 vizinhas")
	void adjacentPositionsMiddle() {
		List<IPosition> adjacents = position.adjacentPositions();
		assertEquals(8, adjacents.size(), "A position in the middle of the board should have 8 neighbours");
		for (IPosition adj : adjacents) {
			assertTrue(position.isAdjacentTo(adj), "Every returned position should be adjacent: " + adj);
			assertNotEquals(position, adj, "A position should not be its own neighbour");
		}
	}

	@Test
	@DisplayName("adjacentPositions: um canto só tem 3 vizinhas dentro do tabuleiro")
	void adjacentPositionsCorner() {
		List<IPosition> adjacents = new Position(0, 0).adjacentPositions();
		assertEquals(3, adjacents.size(), "The corner (0,0) should have 3 neighbours inside the board");
		assertTrue(adjacents.contains(new Position(0, 1)), "(0,1) should be a neighbour of (0,0)");
		assertTrue(adjacents.contains(new Position(1, 0)), "(1,0) should be a neighbour of (0,0)");
		assertTrue(adjacents.contains(new Position(1, 1)), "(1,1) should be a neighbour of (0,0)");
	}

	@Test
	@DisplayName("adjacentPositions: uma posição na borda tem 5 vizinhas")
	void adjacentPositionsEdge() {
		List<IPosition> adjacents = new Position(0, 5).adjacentPositions();
		assertEquals(5, adjacents.size(), "A position on the top edge should have 5 neighbours");
	}

	@Test
	@DisplayName("equals: linha diferente mas mesma coluna não são iguais")
	void equals6() {
		Position other = new Position(3, 3);
		assertFalse(position.equals(other), "Positions with different rows should not be equal");
	}

	@Test
	@DisplayName("equals: o estado (ocupada/atingida) não conta para a igualdade")
	void equalsIgnoresState() {
		Position other = new Position(2, 3);
		other.occupy();
		other.shoot();
		assertTrue(position.equals(other), "Equality should depend only on row and column");
	}
}
