package ru.vsu.dominoes.db;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;

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

  private boolean isPlayerExist(PlayerDataBase playerDataBase) throws SQLException {
    try (Connection c = getConnection()) {
      PreparedStatement statement = c.prepareStatement("select count(*) from player where player_name = ?");
      statement.setString(1, playerDataBase.getName());
      ResultSet rs = statement.executeQuery();
      rs.next();

      return rs.getInt(1) == 0;
    }
  }

  private List<PlayerDataBase> savePlayersToDataBase(List<PlayerDataBase> playersDB) throws SQLException {
    List<PlayerDataBase> playersDBResult = new ArrayList<>();
    Connection c = null;
    try {
      c = getConnection();
      c.setAutoCommit(false);
      PreparedStatement statement = null;
      ResultSet rs = null;

      for (PlayerDataBase playerDataBase : playersDB) {
        if (isPlayerExist(playerDataBase)) {
          statement = c.prepareStatement("insert into player (player_name, player_wins, player_defeats) values (?, ?, ?)");
          statement.setString(1, playerDataBase.getName());
          statement.setInt(2, playerDataBase.getWin());
          statement.setInt(3, playerDataBase.getDefeat());
          statement.executeUpdate();

          statement = c.prepareStatement("select player_id from player where player_name = ?");
          statement.setString(1, playerDataBase.getName());
          rs = statement.executeQuery();
          rs.next();

          playerDataBase.setId(rs.getInt(1));
          playersDBResult.add(playerDataBase);
        } else {
          statement = c.prepareStatement("select player_wins, player_defeats, player_id from player where player_name = ?");
          statement.setString(1, playerDataBase.getName());
          rs = statement.executeQuery();
          rs.next();

          PlayerDataBase updatedPlayerDataBase = new PlayerDataBase(playerDataBase.getName(),
                  rs.getInt(1) + playerDataBase.getWin(),
                  rs.getInt(2) + playerDataBase.getDefeat());

          statement = c.prepareStatement("update player set player_wins = ?, player_defeats = ? where player_name = ?");
          statement.setInt(1, updatedPlayerDataBase.getWin());
          statement.setInt(2, updatedPlayerDataBase.getDefeat());
          statement.setString(3, updatedPlayerDataBase.getName());
          statement.executeUpdate();

          updatedPlayerDataBase.setId(rs.getInt(3));
          playersDBResult.add(updatedPlayerDataBase);
        }
      }

      assert rs != null;
      rs.close();
      statement.close();
      c.commit();
    } catch (SQLException exception) {
      assert c != null;
      c.rollback();

      throw new RuntimeException("Cannot save player", exception);
    } finally {
      assert c != null;
      c.close();
    }

    return playersDBResult;
  }

  private PlayerDataBase savePlayerToDataBase(PlayerDataBase playerDataBase) throws SQLException {
    return savePlayersToDataBase(new ArrayList<>() {{
      add(playerDataBase);
    }}).get(0);
  }

  @Override
  public List<PlayerDataBase> savePlayers(List<PlayerDataBase> playerDataBases) {
    try {
      return savePlayersToDataBase(playerDataBases);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save player", exception);
    }
  }

  @Override
  public PlayerDataBase savePlayer(PlayerDataBase playerDataBase) {
    try {
      return savePlayerToDataBase(playerDataBase);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save player", exception);
    }
  }

  public List<PlayerDataBase> getPlayersFromDataBase(List<String> names) throws SQLException {
    try (Connection c = getConnection()) {
      List<PlayerDataBase> playerDataBases = new ArrayList<>();
      for (String name : names) {
        PreparedStatement statement = c.prepareStatement("select player_id, player_wins, player_defeats from player where player_name = ?");
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        rs.next();
        playerDataBases.add(new PlayerDataBase(name, rs.getInt(2), rs.getInt(3), rs.getInt(1)));
      }

      return playerDataBases;
    }
  }

  private PlayerDataBase getPlayerFromDataBase(String name) throws SQLException {
    return getPlayersFromDataBase(new ArrayList<>() {{
      add(name);
    }}).get(0);
  }

  @Override
  public List<PlayerDataBase> getPlayers(List<String> names) {
    try {
      return getPlayersFromDataBase(names);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get players", exception);
    }
  }

  @Override
  public PlayerDataBase getPlayer(String name) {
    try {
      return getPlayerFromDataBase(name);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get player", exception);
    }
  }

  @Override
  public List<List<GameStatistic>> getLastGames(Integer countGames) {
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

        List<List<GameStatistic>> gameStats = new ArrayList<>();
        for (int i = 0; i < gameId.size(); i++) {
          List<GameStatistic> gameStatistic = new ArrayList<>();
          statement = c.prepareStatement("select player_name, player_score from game_score " +
                  "join player on game_score.player_id = player.player_id " +
                  "where game_id = ?");
          statement.setInt(1, gameId.get(i));

          rs = statement.executeQuery();
          while (rs.next()) {
            gameStatistic.add(new GameStatistic(rs.getString(1), rs.getInt(2), dates.get(i), gameId.get(i)));
          }
          gameStats.add(gameStatistic);
        }

        return gameStats;
      }
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot get last %d game(s)".formatted(countGames), exception);
    }
  }

  public List<GameStatistic> saveGameToDataBase(List<GameStatistic> gameStatistics) throws SQLException {
    Connection c = null;
    try {
      c = getConnection();
      c.setAutoCommit(false);
      PreparedStatement statement = c.prepareStatement("insert into game_history (game_time_end) values (CURRENT_TIMESTAMP)");
      statement.executeUpdate();

      List<PlayerDataBase> playerDataBases = new ArrayList<>();
      List<Integer> scores = new ArrayList<>();

      for (GameStatistic gameStatistic : gameStatistics) {
        playerDataBases.add(getPlayer(gameStatistic.getPlayerName()));
        scores.add(gameStatistic.getScore());
      }

      statement = c.prepareStatement("select game_id, game_time_end from game_history order by game_id desc limit 1");
      ResultSet rs = statement.executeQuery();
      rs.next();
      int lastGame = rs.getInt(1);
      String lastDate = rs.getString(2);

      for (int i = 0; i < playerDataBases.size(); i++) {
        PlayerDataBase playerDataBase = playerDataBases.get(i);
        Integer score = scores.get(i);
        statement = c.prepareStatement("insert into game_score (game_id, player_id, player_score) values (" +
                "?, " +
                "?, " +
                "?)");
        statement.setInt(1, lastGame);
        statement.setInt(2, playerDataBase.getId());
        statement.setInt(3, score);
        statement.executeUpdate();
      }

      for (GameStatistic gameStatistic : gameStatistics) {
        gameStatistic.setId(lastGame);
        gameStatistic.setDate(lastDate);
      }

      rs.close();
      statement.close();
      c.commit();
    } catch (SQLException exception) {
      assert c != null;
      c.rollback();
      throw new RuntimeException("Cannot save game", exception);
    } finally {
      assert c != null;
      c.close();
    }

    return gameStatistics;
  }

  @Override
  public List<GameStatistic> saveGame(List<GameStatistic> gameStatistics) {
    try {
      return saveGameToDataBase(gameStatistics);
    } catch (SQLException exception) {
      throw new RuntimeException("Cannot save game", exception);
    }
  }
}
