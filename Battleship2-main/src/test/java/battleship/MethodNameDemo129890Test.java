package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MethodNameDemo129890.
 * Author: Rafael Balão (129890)
 * Cyclomatic Complexity:
 * - Constructor: 1
 * - calcularPontos: 1
 */
@DisplayName("Testes da classe MethodNameDemo129890")
class MethodNameDemo129890Test {

	@Test
	@DisplayName("calcularPontos dá 10 pontos por cada acerto")
	void calcularPontos() {
		assertEquals(0, MethodNameDemo129890.calcularPontos(0), "Error: 0 hits should give 0 points.");
		assertEquals(10, MethodNameDemo129890.calcularPontos(1), "Error: 1 hit should give 10 points.");
		assertEquals(170, MethodNameDemo129890.calcularPontos(17), "Error: 17 hits should give 170 points.");
	}

	@Test
	@DisplayName("calcularPontos com valor negativo devolve pontos negativos")
	void calcularPontosNegativo() {
		assertEquals(-20, MethodNameDemo129890.calcularPontos(-2), "Error: -2 hits should give -20 points.");
	}

	@Test
	@DisplayName("A classe utilitária não pode ser instanciada de fora (construtor privado)")
	void constructorIsPrivate() throws Exception {
		Constructor<MethodNameDemo129890> constructor = MethodNameDemo129890.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Error: The constructor should be private.");
		assertTrue(Modifier.isFinal(MethodNameDemo129890.class.getModifiers()), "Error: The class should be final.");
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance(), "Error: The private constructor should still create an instance.");
	}
}
