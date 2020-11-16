package ru.vsu.dominoes.db.model;

import java.util.List;

public class GameStats {
  private List<String> playerNames;
  private List<Integer> scores;
  private String date;

  public GameStats(List<String> playerNames, List<Integer> scores) {
    this.playerNames = playerNames;
    this.scores = scores;
  }

  public List<String> getPlayerNames() {
    return playerNames;
  }

  public void setPlayerNames(List<String> playerNames) {
    this.playerNames = playerNames;
  }

  public List<Integer> getScores() {
    return scores;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Game from ").append(date, 11, 16)
            .append(" ")
            .append(date, 0, 10)
            .append("\n").
            append("Names");

    int index = 0;
    for (String playerName : playerNames) {
      if (index++ != 0) {
        stringBuilder.append(", ");
      } else {
        stringBuilder.append(" ");
      }

      stringBuilder.append(playerName);
    }
    stringBuilder.append(". ").append("Scores");
    index = 0;
    for (Integer score : scores) {
      if (index++ != 0) {
        stringBuilder.append(", ");
      } else {
        stringBuilder.append(" ");
      }

      stringBuilder.append(score);
    }
    return stringBuilder.append("\n").toString();
  }

  public void setScores(List<Integer> scores) {
    this.scores = scores;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
