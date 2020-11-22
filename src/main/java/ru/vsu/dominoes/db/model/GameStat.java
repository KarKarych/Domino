package ru.vsu.dominoes.db.model;

public class GameStat {
  private final String playerName;
  private final Integer score;
  private String date;

  public GameStat(String playerName, Integer score) {
    this.playerName = playerName;
    this.score = score;
  }

  public GameStat(String playerName, Integer score, String date) {
    this.playerName = playerName;
    this.score = score;
    this.date = date;
  }

  public String getPlayerName() {
    return playerName;
  }

  public Integer getScore() {
    return score;
  }

  public String getDate() {
    return date;
  }
}
