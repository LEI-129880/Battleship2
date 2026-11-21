# Oportunidades de refabricação

Ficha 4, Parte 2. Cada linha indica onde está o cheiro no código, que refabricação
do IntelliJ o resolve e quem a aplica no seu ramo `Refactoring_<número>`.

| Local | Cheiro no código | Refabricação (IntelliJ) | Nº alun@ |
|---|---|---|---|
| `Compass::c` | Mysterious Name (nome do campo não diz o que guarda) | Rename | 129890 |
| `Ship::getAdjacentPositions` | Variável temporária redundante (`adjacents`, usada uma só vez) | Inline Variable | 129890 |
| `Position::isAdjacentTo` | Expressão condicional complexa (distâncias calculadas dentro da condição) | Introduce Variable | 129890 |
| `Galleon::Galleon` | Magic Number (tamanho `5` passado diretamente ao construtor) | Introduce Constant | 129890 |
| `Galleon::fillNorth/fillSouth/fillEast/fillWest` | Duplicated Code (`getPositions().add(new Position(...))` repetido 11 vezes) | Extract Method | 129890 |
| `Ship::positions` | Data Class / encapsulamento fraco (campo `protected`, mas todos os acessos usam o getter) | Encapsulate Fields | 129890 |
| `Fleet::colisionRisk` | Mysterious Name | Rename | 129880 |
| `Fleet::colisionRisk` | Loop indexado desnecessário | Replace with enhanced for | 129880 |
| `Game::randomEnemyFire` | Long Method | Extract Method | 129880 |
| `Game::printBoardShots` | Complex Conditional | Introduce Variable | 129880 |
| `Tasks::menu` | Magic Number (3000) | Introduce Constant | 129880 |
