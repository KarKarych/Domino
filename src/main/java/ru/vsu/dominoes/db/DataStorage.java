package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStat;
import ru.vsu.dominoes.db.model.PlayerDB;

import java.util.List;

public interface DataStorage {
  void savePlayer(PlayerDB playerDB);

  PlayerDB getPlayer(String names);

  void savePlayers(List<PlayerDB> playerDBS);

  List<PlayerDB> getPlayers(List<String> names);

  void saveGame(List<GameStat> gameStats);

  List<List<GameStat>> getLastGames(Integer countGames);
}
