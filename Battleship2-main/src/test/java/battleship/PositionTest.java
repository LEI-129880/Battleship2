package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Position.
 * Author: britoeabreu
 * Date: 2024-03-19 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getRow: 1
 * - getColumn: 1
 * - isValid: 4
 * - isAdjacentTo: 4
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
@DisplayName("Testes da classe Position")
public class PositionTest {
	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	//	position = new Position('C', 4);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	@Test
	@DisplayName("O construtor numérico guarda linha e coluna e começa livre e sem tiros")
	void constructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
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
	@DisplayName("getRow devolve a linha")
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	@DisplayName("getColumn devolve a coluna")
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	@Test
	@DisplayName("getClassicRow devolve a letra da linha")
	void getClassicRow() {
		assertEquals('C', position.getClassicRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	@DisplayName("getClassicColumn devolve a coluna a contar de 1")
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn(), "Failed to get classic column: expected 4 but got " + position.getClassicColumn());
	}

	@Test
	@DisplayName("isInside: a posição (0,0) está dentro do tabuleiro")
	void isValid1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be valid");
	}

	@Test
	@DisplayName("isInside: linha negativa está fora do tabuleiro")
	void isValid2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be invalid");
	}

	@Test
	@DisplayName("isInside: coluna negativa está fora do tabuleiro")
	void isValid3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be invalid");
	}

	@Test
	@DisplayName("isInside: linha igual a BOARD_SIZE está fora do tabuleiro")
	void isValid4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row >= BOARD_SIZE should be invalid");
	}

	@Test
	@DisplayName("isInside: coluna igual a BOARD_SIZE está fora do tabuleiro")
	void isValid5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column >= BOARD_SIZE should be invalid");
	}

	@Test
	@DisplayName("isInside: o canto inferior direito ainda está dentro do tabuleiro")
	void isValid6() {
		position = new Position(Game.BOARD_SIZE - 1, Game.BOARD_SIZE - 1);
		assertTrue(position.isInside(), "Position (BOARD_SIZE-1, BOARD_SIZE-1) should be valid");
	}

	@Test
	@DisplayName("isAdjacentTo: deteta vizinho na horizontal")
	void isAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect horizontally adjacent position");
	}

	@Test
	@DisplayName("isAdjacentTo: deteta vizinho na vertical")
	void isAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Failed to detect vertically adjacent position");
	}

	@Test
	@DisplayName("isAdjacentTo: deteta vizinho na diagonal")
	void isAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect diagonally adjacent position");
	}

	@Test
	@DisplayName("isAdjacentTo: linha a mais de 1 de distância não é vizinha")
	void isAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Non-adjacent position incorrectly identified as adjacent");
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
	@DisplayName("isAdjacentTo: lança NullPointerException com argumento nulo")
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input");
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
	@DisplayName("occupy marca a posição como ocupada")
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
	}

	@Test
	@DisplayName("shoot marca a posição como atingida")
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot()");
	}

	@Test
	@DisplayName("equals: posições com a mesma linha e coluna são iguais")
	void equals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Equal positions not identified as equal");
	}

	@Test
	@DisplayName("equals: uma posição não é igual a null")
	void equals2() {
		assertFalse(position.equals(null), "Position should not equal null");
	}

	@Test
	@DisplayName("equals: uma posição não é igual a um objeto de outro tipo")
	void equals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Position should not equal non-Position object");
	}

	@Test
	@DisplayName("equals: mesma linha mas coluna diferente não são iguais")
	void equals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Positions with the same row but different column should not be equal");
	}

	@Test
	@DisplayName("equals: uma posição é igual a si própria")
	void equals5() {
		assertTrue(position.equals(position), "A position should be equal to itself");
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

	@Test
	@DisplayName("hashCode é igual para posições iguais com o mesmo estado")
	void hashCodeConsistency() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(),
				"Hash codes not consistent for equal positions");
	}

	@Test
	@DisplayName("toString devolve a notação clássica (ex.: C4)")
	void toStringFormat() {
//		String expected = "Row = C, Column = 4";
		String expected = "C4";
		assertEquals(expected, position.toString(),
				"Incorrect string representation: expected '" + expected +
						"' but got '" + position.toString() + "'");
	}
}
