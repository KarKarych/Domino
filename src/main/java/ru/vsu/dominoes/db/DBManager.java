package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStats;
import ru.vsu.dominoes.db.model.PlayerDB;
import ru.vsu.dominoes.model.Game;
import ru.vsu.dominoes.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
  public void saveResults(Game game){
    Player[] boardPlayers = game.getPlayers();
    DataStorage dataStorage = new DataBaseDataStorage();
    List<PlayerDB> players = new ArrayList<>();
    List<String> playersNames = new ArrayList<>();
    List<Integer> scores = new ArrayList<>();
    for (Player boardPlayer : boardPlayers) {
      PlayerDB playerTemp = new PlayerDB(boardPlayer.getName(), 0, 0);
      playersNames.add(boardPlayer.getName());
      if (!game.getWinners().contains(boardPlayer)) {
        playerTemp.setDefeat(1);
      } else {
        playerTemp.setWin(1);
      }

      players.add(playerTemp);
      scores.add(Game.calculateScore(boardPlayer));

    }
    dataStorage.savePlayers(players);
    dataStorage.saveGame(new GameStats(playersNames, scores));

    List<GameStats> gameStats = dataStorage.getLastGames(5);
    for (GameStats gameStat : gameStats) {
      System.out.println(gameStat);
    }

    for (PlayerDB playerDB : dataStorage.getPlayers(playersNames)) {
      System.out.println(playerDB);
    }
  }
}
