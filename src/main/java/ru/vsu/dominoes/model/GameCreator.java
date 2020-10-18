package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.ui.ConsoleUI;
import ru.vsu.dominoes.ui.GameUI;
import ru.vsu.dominoes.utils.Names;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameCreator {
  public GameCreator() {
    GameUI gameUI = new ConsoleUI();

    int[] countPlayers = gameUI.getCountOfPlayersFromUser();
    if (countPlayers != null) {
      String[] namesOfPlayers = gameUI.getNamesOfPlayersFromUser(countPlayers[0]);
      Player[] players = initialisePlayers(countPlayers, namesOfPlayers);

      Board board = new Board(players.length);
      board.setPlayers(players);
      Market market = board.getMarket();

      for (Player player : players) {
        player.setBoard(board);
        market.handOutChips(player);
      }

      Game game = new Game(board);
      game.startGame();
    }
  }

  private Player[] initialisePlayers(int[] countPlayers, String[] namesOfPlayers) {
    Player[] players = new Player[countPlayers[0] + countPlayers[1]];
    List<Integer> names = new LinkedList<>();
    for (int i = 0; i < players.length; ++i) {
      if (i < countPlayers[0]) {
        players[i] = new HumanPlayer(namesOfPlayers[i]);
      } else {
        int index;
        do {
          index = new Random().nextInt(Names.NAMES.length - 1);
        } while (names.contains(index));
        names.add(index);

        players[i] = new AIPlayer(Names.NAMES[index]);
      }
    }

    return players;
  }
}
