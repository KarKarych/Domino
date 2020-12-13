package ru.vsu.dominoes.db.model;

import java.util.Objects;

public class GameStatistic implements Comparable<GameStatistic> {
  private final String playerName;
  private final Integer score;
  private String date;
  private Integer id;

  public GameStatistic(String playerName, Integer score) {
    this.playerName = playerName;
    this.score = score != null ? score : 0;
  }

  public GameStatistic(String playerName, Integer score, String date, Integer id) {
    this.playerName = playerName;
    this.score = score != null ? score : 0;
    this.date = date;
    this.id = id;
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

  public void setDate(String date) {
    this.date = date;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerName, score, date);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameStatistic that = (GameStatistic) o;
    return Objects.equals(playerName, that.playerName) &&
            Objects.equals(score, that.score) &&
            Objects.equals(date, that.date) &&
            Objects.equals(id, that.id);
  }

  @Override
  public int compareTo(GameStatistic gameStatistic) {
    return gameStatistic.playerName.compareTo(playerName);
  }
}
