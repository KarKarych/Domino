package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;

import java.util.List;

public interface DataStorage {
  PlayerDataBase savePlayer(PlayerDataBase playerDataBase);

  PlayerDataBase getPlayer(String names);

  List<PlayerDataBase> savePlayers(List<PlayerDataBase> playerDataBases);

  List<PlayerDataBase> getPlayers(List<String> names);

  List<GameStatistic> saveGame(List<GameStatistic> gameStatistics);

  List<List<GameStatistic>> getLastGames(Integer countGames);
}
