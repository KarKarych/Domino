package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;
import ru.vsu.dominoes.model.Game;
import ru.vsu.dominoes.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
  public void saveResults(Game game, Player[] boardPlayers){
    DataStorage dataStorage = new DataBaseDataStorage();
    List<PlayerDataBase> players = new ArrayList<>();
    List<GameStatistic> gameStatistics = new ArrayList<>();
    for (Player boardPlayer : boardPlayers) {
      PlayerDataBase playerTemp = new PlayerDataBase(boardPlayer.getName(), 0, 0);
      if (!game.getWinners().contains(boardPlayer)) {
        playerTemp.setDefeat(1);
      } else {
        playerTemp.setWin(1);
      }
      players.add(playerTemp);

      gameStatistics.add(new GameStatistic(boardPlayer.getName(), Game.calculateScore(boardPlayer)));
    }

    dataStorage.savePlayers(players);
    dataStorage.saveGame(gameStatistics);
  }

  public List<List<GameStatistic>> getLastGames(int countGames){
    DataStorage dataStorage = new DataBaseDataStorage();
    return dataStorage.getLastGames(countGames);
  }

  public List<PlayerDataBase> getPlayers(List<String> playersNames){
    DataStorage dataStorage = new DataBaseDataStorage();
    return dataStorage.getPlayers(playersNames);
  }
}
