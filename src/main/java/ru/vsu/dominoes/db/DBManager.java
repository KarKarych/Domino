package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStat;
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
    List<GameStat> gameStats = new ArrayList<>();
    for (Player boardPlayer : boardPlayers) {
      PlayerDB playerTemp = new PlayerDB(boardPlayer.getName(), 0, 0);
      if (!game.getWinners().contains(boardPlayer)) {
        playerTemp.setDefeat(1);
      } else {
        playerTemp.setWin(1);
      }
      players.add(playerTemp);

      gameStats.add(new GameStat(boardPlayer.getName(), Game.calculateScore(boardPlayer)));
    }

    dataStorage.savePlayers(players);
    dataStorage.saveGame(gameStats);
  }

  public List<List<GameStat>> getLastGames(int countGames){
    DataStorage dataStorage = new DataBaseDataStorage();
    return dataStorage.getLastGames(countGames);
  }

  public List<PlayerDB> getPlayers(List<String> playersNames){
    DataStorage dataStorage = new DataBaseDataStorage();
    return dataStorage.getPlayers(playersNames);
  }
}
