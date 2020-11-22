package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStat;
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

  private void savePlayersToDataBase(List<PlayerDB> playersDB) throws SQLException {
    Connection c = null;
    try {
      c = getConnection();
      c.setAutoCommit(false);
      PreparedStatement statement = null;
      for (PlayerDB playerDB : playersDB) {
        if (isPlayerExist(playerDB)) {
          statement = c.prepareStatement("insert into player (player_name, player_wins, player_defeats) values (?, ?, ?)");
          statement.setString(1, playerDB.getName());
          statement.setInt(2, playerDB.getWin());
          statement.setInt(3, playerDB.getDefeat());
        } else {
          statement = c.prepareStatement("select player_wins, player_defeats from player where player_name = ?");
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
          rs.close();
        }
        statement.executeUpdate();
      }

      assert statement != null;
      statement.close();
      c.commit();
    } catch (SQLException exception) {
      assert c != null;
      c.rollback();

      throw new RuntimeException("Cannot save player", exception);
    } finally{
      assert c != null;
      c.close();
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
  public List<List<GameStat>> getLastGames(Integer countGames) {
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

        List<List<GameStat>> gameStats = new ArrayList<>();
        for (int i = 0; i < gameId.size(); i++) {
          List<GameStat> gameStat = new ArrayList<>();
          statement = c.prepareStatement("select player_name, player_score from game_score " +
                  "join player on game_score.player_id = player.player_id " +
                  "where game_id = ?");
          statement.setInt(1, gameId.get(i));

          rs = statement.executeQuery();
          while (rs.next()) {
            gameStat.add(new GameStat(rs.getString(1), rs.getInt(2), dates.get(i)));
          }
          gameStats.add(gameStat);
        }

        return gameStats;
      }
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get last %d game(s)".formatted(countGames), exception);
    }
  }

  public void saveGameToDataBase(List<GameStat> gameStats) throws SQLException {
    Connection c = null;
    try {
      c = getConnection();
      c.setAutoCommit(false);
      PreparedStatement statement = c.prepareStatement("insert into game_history (game_time_end) values (CURRENT_TIMESTAMP)");
      statement.executeUpdate();

      List<PlayerDB> playerDBS = new ArrayList<>();
      List<Integer> scores= new ArrayList<>();

      for (GameStat gameStat : gameStats) {
        playerDBS.add(getPlayer(gameStat.getPlayerName()));
        scores.add(gameStat.getScore());
      }

      for (int i = 0; i < playerDBS.size(); i++) {
        PlayerDB playerDB = playerDBS.get(i);
        Integer score = scores.get(i);
        statement = c.prepareStatement("insert into game_score (game_id, player_id, player_score) values (" +
                "(select game_id from game_history order by game_id desc limit 1), " +
                "?, " +
                "?)");
        statement.setInt(1, playerDB.getId());
        statement.setInt(2, score);
        statement.executeUpdate();
      }

      statement.close();
      c.commit();
    } catch (SQLException exception) {
      assert c != null;
      c.rollback();
      throw new RuntimeException("Cannot save game", exception);
    } finally{
      assert c != null;
      c.close();
    }
  }

  @Override
  public void saveGame(List<GameStat> gameStats) {
    try {
      saveGameToDataBase(gameStats);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save game", exception);
    }
  }
}
