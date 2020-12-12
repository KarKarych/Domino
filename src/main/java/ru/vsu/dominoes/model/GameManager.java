package ru.vsu.dominoes.model;

import ru.vsu.dominoes.db.DataBaseManager;
import ru.vsu.dominoes.model.enums.GameType;
import ru.vsu.dominoes.model.enums.PlayerType;
import ru.vsu.dominoes.model.game.Game;
import ru.vsu.dominoes.model.game.GameLAN;
import ru.vsu.dominoes.model.game.GameOneDevice;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.p2p.Peer;
import ru.vsu.dominoes.parser.JsonParser;
import ru.vsu.dominoes.ui.ConsoleUI;
import ru.vsu.dominoes.ui.GameUI;
import ru.vsu.dominoes.utils.GameData;

public class GameManager {
  private final GameUI gameUI;
  private final DataBaseManager dataBaseManager;

  public GameManager() {
    gameUI = new ConsoleUI();
    dataBaseManager = new DataBaseManager();
  }

  public void play() {
    GameType gameType = gameUI.choiceTypeGame();

    if (gameType == null) return;

    if (gameType.equals(GameType.ONE_DEVICE)) {
      playOneDevice();
    } else {
      playLAN();
    }
  }

  private void playLAN() {
    PlayerCreator playerCreator = new PlayerCreator(gameUI);
    GameData gameData = playerCreator.initialisePlayersLAN();

    if (gameData == null) return;

    Peer peer = gameData.getPeer();
    Player[] players = gameData.getPlayers();
    JsonParser jsonParser = new JsonParser();
    Board board = new Board(players.length);

    if (gameData.getPlayerType().equals(PlayerType.HOST)) {
      board.setPlayers(players);
      Market market = board.getMarket();

      peer.send(jsonParser.parseMarketJson(market.getChips()));

      for (Player player : players) {
        player.setBoard(board);
        market.handOutChips(player);
      }
    } else {
      board.setPlayers(players);
      Market market = board.getMarket();

      market.setChips(jsonParser.parseMarketList(peer.get()));

      for (Player player : players) {
        player.setBoard(board);
        market.handOutChips(player);
      }
    }

    Game game = new GameLAN(board, new ConsoleUI(board), peer, gameData.getPlayerType());
    game.play();

    if (gameData.getPlayerType().equals(PlayerType.HOST)) {
      dataBaseManager.saveResults(game, board.getPlayers());
      printResults(players);
      peer.send("1");
    } else {
      if (Integer.parseInt(peer.get()) == 1) {
        printResults(players);
      }
    }
  }

  private void playOneDevice() {
    PlayerCreator playerCreator = new PlayerCreator(gameUI);
    Player[] players = playerCreator.initialisePlayersOneDevice();

    Board board = new Board(players.length);
    board.setPlayers(players);
    Market market = board.getMarket();

    for (Player player : players) {
      player.setBoard(board);
      market.handOutChips(player);
    }

    Game game = new GameOneDevice(board, new ConsoleUI(board));
    game.play();

    dataBaseManager.saveResults(game, board.getPlayers());

    printResults(players);
  }

  private void printResults(Player[] players) {
    gameUI.printResultsOfLastGames(dataBaseManager.getLastGames(5));
    gameUI.printStatsOfPlayers(dataBaseManager.getPlayers(players));
  }
}
