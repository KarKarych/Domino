package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStats;
import ru.vsu.dominoes.db.model.PlayerDB;

import java.util.List;

public interface DataStorage {
  void savePlayer(PlayerDB playerDB);

  PlayerDB getPlayer(String names);

  void savePlayers(List<PlayerDB> playerDBS);

  List<PlayerDB> getPlayers(List<String> names);

  void saveGame(GameStats gameStat);

  List<GameStats> getLastGames(Integer countGames);
}
