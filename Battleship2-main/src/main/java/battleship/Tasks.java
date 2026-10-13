package battleship;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The type Tasks.
 */
public class Tasks {
	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The constant GOODBYE_MESSAGE.
	 */
	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	/**
	 * Strings to be used by the user
	 */
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String MAPA = "mapa";
	private static final String TABULEIRO = "tabuleiro";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";

	/**
	 * This task also tests the fighting element of a round of three shots
	 */
	public static void menu() {
        BoardWindow[] window = new BoardWindow[1];
        onBoard(() -> {
            com.formdev.flatlaf.FlatLightLaf.setup();
            window[0] = new BoardWindow();
            window[0].setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
        });
        Scanner in = new Scanner(System.in);
        menuHelp();
        try {
            while (true) {
                System.out.print("> ");
                if (!in.hasNext()) break;
                String command = in.next().toLowerCase(java.util.Locale.ROOT);
                if (command.equals(DESISTIR)) break;
                try {
                    switch (command) {
                        case GERAFROTA -> {
                            IFleet fleet = Fleet.createRandom();
                            onBoard(() -> {
                                window[0].consoleFleet(fleet);
                                window[0].consoleGame().printMyBoard(false, true);
                            });
                        }
                        case LEFROTA -> {
                            IFleet fleet = buildFleet(in);
                            onBoard(() -> window[0].consoleFleet(fleet));
                        }
                        case TABULEIRO -> onBoard(() -> {
                            window[0].setVisible(true);
                            window[0].toFront();
                        });
                        case MAPA -> onBoard(() -> window[0].consoleGame().printMyBoard(false, true));
                        case STATUS -> onBoard(() -> window[0].consoleGame().getMyFleet().printStatus());
                        case TIROS -> onBoard(() -> window[0].consoleGame().printMyBoard(true, true));
                        case RAJADA -> {
                            String input = in.nextLine().trim();
                            if (input.isEmpty()) {
                                System.out.println("Indica três posições, por exemplo: A1 B2 C3");
                                if (!in.hasNextLine()) break;
                                input = in.nextLine();
                            }
                            java.util.List<IPosition> shots = parseShots(input);
                            onBoard(() -> {
                                window[0].consoleShots(shots);
                                window[0].consoleGame().printMyBoard(true, true);
                            });
                        }
                        case SIMULA -> {
                            while (true) {
                                boolean[] next = {false};
                                onBoard(() -> {
                                    if (window[0].consoleGame().getMyFleet().getShips().size() != IFleet.FLEET_SIZE)
                                        throw new IllegalStateException("Gera ou coloca primeiro os 11 navios.");
                                    next[0] = window[0].consoleGame().getRemainingShips() > 0;
                                    if (next[0]) {
                                        window[0].consoleRandomShots();
                                        window[0].consoleGame().printMyBoard(true, false);
                                    }
                                });
                                if (!next[0]) break;
                                Thread.sleep(3000);
                            }
                        }
                        case AJUDA -> menuHelp();
                        default -> System.out.println("Comando desconhecido. Escreve ajuda.");
                    }
                } catch (IllegalArgumentException | IllegalStateException error) {
                    System.out.println(error.getMessage());
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            onBoard(() -> window[0].dispose());
        }
        System.out.println(GOODBYE_MESSAGE);
    }

    static java.util.List<IPosition> parseShots(String input) {
        java.util.List<IPosition> shots = new java.util.ArrayList<>();
        try (Scanner tokens = new Scanner(input)) {
            while (tokens.hasNext()) shots.add(readClassicPosition(tokens));
        }
        if (shots.size() != Game.NUMBER_SHOTS || shots.stream().anyMatch(p -> !p.isInside()))
            throw new IllegalArgumentException("Indica três posições entre A1 e J10, por exemplo: rajada A1 B2 C3.");
        return shots;
    }

    private static void onBoard(Runnable action) {
        try {
            javax.swing.SwingUtilities.invokeAndWait(action);
        } catch (java.lang.reflect.InvocationTargetException error) {
            if (error.getCause() instanceof RuntimeException cause) throw cause;
            throw new IllegalStateException(error.getCause());
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operação interrompida.", error);
        }
    }

	/**
	 * This function provides help information about the menu commands.
	 */
	public static void menuHelp() {
		System.out.println("======================= AJUDA DO MENU =========================");
		System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
		System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
		System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
		System.out.println("- " + STATUS + ": Mostra o status atual da frota.)");
		System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
		System.out.println("- " + TABULEIRO + ": Abre a janela da mesma partida, com os dois tabuleiros.");
		System.out.println("- " + RAJADA + ": Regista tiros recebidos na tua frota: rajada A1 B2 C3.");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água)");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("===============================================================");
	}
	/**
	 * This operation allows the build up of a fleet, given user data
	 *
	 * @param in The scanner to read from
	 * @return The fleet that has been built
	 */
	public static Fleet buildFleet(Scanner in) {

		assert in != null;

		Fleet fleet = new Fleet();
		int i = 0; // i represents the total of successfully created ships
		while (i < Fleet.FLEET_SIZE) {
			IShip s = readShip(in);
			if (s != null) {
				boolean success = fleet.addShip(s);
				if (success)
					i++;
				else
					LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}
		LOGGER.info("{} navios adicionados com sucesso!", i);
		return fleet;
	}

	/**
	 * This operation reads data about a ship, build it and returns it
	 *
	 * @param in The scanner to read from
	 * @return The created ship based on the data that has been read
	 */
	public static Ship readShip(Scanner in) {

		assert in != null;

		String shipKind = in.next();
		Position pos = readPosition(in);
		char c = in.next().charAt(0);
		Compass bearing = Compass.charToCompass(c);
		return Ship.buildShip(shipKind, bearing, pos);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The position that has been read
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();
		return new Position(row, column);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The classic position that has been read
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {
		// Verifica se ainda há tokens disponíveis
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next(); // Primeiro token
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next(); // Segundo token, se disponível
		}

		String input = (part2 != null) ? part1 + part2 : part1;

		// Normalizar o input para tratar letras maiúsculas e minúsculas
		input = input.toUpperCase();

		// Verificar os dois formatos possíveis: compactos e com espaço
		if (input.matches("[A-Z]\\d+")) {
			char column = input.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(input.substring(1)); // Extrair a linha
			return new Position(column, row);
		} else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
			char column = part1.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(part2); // Extrair a linha
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}

}
