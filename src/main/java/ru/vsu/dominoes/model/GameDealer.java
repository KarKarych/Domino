package ru.vsu.dominoes.model;

import ru.vsu.dominoes.db.DataBaseManager;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.ui.ConsoleUI;
import ru.vsu.dominoes.ui.GameUI;

import java.util.ArrayList;
import java.util.List;

public class GameDealer {
  public GameDealer() {
  }

  public void play() {
    GameUI gameUI = new ConsoleUI();
    PlayerCreator playerCreator = new PlayerCreator(gameUI);
    Player[] players = playerCreator.initialisePlayers();

    if (players != null) {
      Board board = new Board(players.length);
      board.setPlayers(players);
      Market market = board.getMarket();

      for (Player player : players) {
        player.setBoard(board);
        market.handOutChips(player);
      }

      Game game = new Game(board, new ConsoleUI(board, true));
      game.play();

      DataBaseManager dataBaseManager = new DataBaseManager();
      dataBaseManager.saveResults(game, board.getPlayers());

      gameUI.printResultsOfLastGames(dataBaseManager.getLastGames(5));

      List<String> playersNames = new ArrayList<>();
      for (Player player : players) {
        playersNames.add(player.getName());
      }
      gameUI.printStatsOfPlayers(dataBaseManager.getPlayers(playersNames));
    }
  }
}
