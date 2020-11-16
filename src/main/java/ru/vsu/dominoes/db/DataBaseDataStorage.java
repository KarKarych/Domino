package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStats;
import ru.vsu.dominoes.db.model.PlayerDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DataBaseDataStorage implements DataStorage {
  private static final ResourceBundle resource = ResourceBundle.getBundle("application");

  private final String url = resource.getString("db.url");
  private final String user = resource.getString("db.user");
  private final String password = resource.getString("db.password");

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  private boolean isPlayerExist(PlayerDB playerDB) throws SQLException {
    try (Connection c = getConnection()) {
      PreparedStatement statement = c.prepareStatement("select count(*) from player where player_name = ?");
      statement.setString(1, playerDB.getName());
      ResultSet rs = statement.executeQuery();
      rs.next();

      return rs.getInt(1) == 0;
    }
  }

  private void savePlayersToDataBase(List<PlayerDB> playerDBS) throws SQLException {
    try (Connection c = getConnection()) {
      for (PlayerDB playerDB : playerDBS) {
        if (isPlayerExist(playerDB)) {
          PreparedStatement statement = c.prepareStatement("insert into player (player_name, player_wins, player_defeats) values (?, ?, ?)");
          statement.setString(1, playerDB.getName());
          statement.setInt(2, playerDB.getWin());
          statement.setInt(3, playerDB.getDefeat());
          statement.executeUpdate();
        } else {
          PreparedStatement statement = c.prepareStatement("select player_wins, player_defeats from player where player_name = ?");
          statement.setString(1, playerDB.getName());
          ResultSet rs = statement.executeQuery();
          rs.next();
          PlayerDB updatedPlayerDB = new PlayerDB(playerDB.getName(),
                  rs.getInt(1) + playerDB.getWin(),
                  rs.getInt(2) + playerDB.getDefeat());

          statement = c.prepareStatement("update player set player_wins = ?, player_defeats = ? where player_name = ?");
          statement.setInt(1, updatedPlayerDB.getWin());
          statement.setInt(2, updatedPlayerDB.getDefeat());
          statement.setString(3, updatedPlayerDB.getName());
          statement.executeUpdate();
        }
      }
    }
  }

  private void savePlayerToDataBase(PlayerDB playerDB) throws SQLException {
    savePlayersToDataBase(new ArrayList<>() {{
      add(playerDB);
    }});
  }

  @Override
  public void savePlayers(List<PlayerDB> playerDBS) {
    try {
      savePlayersToDataBase(playerDBS);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save player", exception);
    }
  }

  @Override
  public void savePlayer(PlayerDB playerDB) {
    try {
      savePlayerToDataBase(playerDB);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save player", exception);
    }
  }

  public List<PlayerDB> getPlayersFromDataBase(List<String> names) throws SQLException {
    try (Connection c = getConnection()) {
      List<PlayerDB> playerDBS = new ArrayList<>();
      for (String name : names) {
        PreparedStatement statement = c.prepareStatement("select player_id, player_wins, player_defeats from player where player_name = ?");
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        rs.next();
        playerDBS.add(new PlayerDB(name, rs.getInt(2), rs.getInt(3), rs.getInt(1)));
      }

      return playerDBS;
    }
  }

  private PlayerDB getPlayerFromDataBase(String name) throws SQLException {
    return getPlayersFromDataBase(new ArrayList<>() {{
      add(name);
    }}).get(0);
  }

  @Override
  public List<PlayerDB> getPlayers(List<String> names) {
    try {
      return getPlayersFromDataBase(names);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get players", exception);
    }
  }


  @Override
  public PlayerDB getPlayer(String name) {
    try {
      return getPlayerFromDataBase(name);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get player", exception);
    }
  }

  @Override
  public List<GameStats> getLastGames(Integer countGames) {
    try {
      try (Connection c = getConnection()) {
        PreparedStatement statement = c.prepareStatement("select game_id, game_time_end from game_history order by game_id desc limit ?");
        statement.setInt(1, countGames);

        List<Integer> gameId = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
          gameId.add(rs.getInt(1));
          dates.add(rs.getString(2));
        }

        List<GameStats> gameStats = new ArrayList<>();
        for (int i = 0; i < gameId.size(); i++) {
          statement = c.prepareStatement("select player_name, player_score from game_score " +
                  "join player on game_score.player_id = player.player_id " +
                  "where game_id = ?");
          statement.setInt(1, gameId.get(i));

          rs = statement.executeQuery();
          List<String> names = new ArrayList<>();
          List<Integer> scores = new ArrayList<>();
          while (rs.next()) {
            names.add(rs.getString(1));
            scores.add(rs.getInt(2));
          }
          GameStats gameStat = new GameStats(names, scores);
          gameStat.setDate(dates.get(i));
          gameStats.add(gameStat);
        }

        return gameStats;
      }
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get last %d game(s)".formatted(countGames), exception);
    }
  }

  @Override
  public void saveGame(GameStats gameStat) {
    try {
      try (Connection c = getConnection()) {
        PreparedStatement statement = c.prepareStatement("insert into game_history (game_time_end) values (CURRENT_TIMESTAMP)");
        statement.executeUpdate();

        statement = c.prepareStatement("select game_id from game_history order by game_id desc limit 1");
        ResultSet rs = statement.executeQuery();
        rs.next();

        int lastId = rs.getInt(1);

        List<PlayerDB> playerDBS = getPlayers(gameStat.getPlayerNames());
        List<Integer> scores = gameStat.getScores();

        for (int i = 0; i < playerDBS.size(); i++) {
          PlayerDB playerDB = playerDBS.get(i);
          Integer score = scores.get(i);
          statement = c.prepareStatement("insert into game_score (game_id, player_id, player_score) values (?, ?, ?)");
          statement.setInt(1, lastId);
          statement.setInt(2, playerDB.getId());
          statement.setInt(3, score);
          statement.executeUpdate();
        }
      }
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save game", exception);
    }
  }
}
