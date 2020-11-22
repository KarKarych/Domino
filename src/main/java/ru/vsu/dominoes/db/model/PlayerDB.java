package ru.vsu.dominoes.db.model;

public class PlayerDB {
  private final String name;
  private Integer win;
  private Integer defeat;
  private Integer id;

  public PlayerDB(String name, Integer win, Integer defeat) {
    this.name = name;
    this.win = win;
    this.defeat = defeat;
  }

  public PlayerDB(String name, Integer win, Integer defeat, Integer id) {
    this.name = name;
    this.win = win;
    this.defeat = defeat;
    this.id = id;
  }

  @Override
  public String toString() {
    return "Player " + name + ". Wins " + win + ". Defeats " + defeat;
  }

  public String getName() {
    return name;
  }

  public Integer getWin() {
    return win;
  }

  public void setWin(Integer win) {
    this.win = win;
  }

  public Integer getDefeat() {
    return defeat;
  }

  public void setDefeat(Integer defeat) {
    this.defeat = defeat;
  }

  public Integer getId() {
    return id;
  }
}
