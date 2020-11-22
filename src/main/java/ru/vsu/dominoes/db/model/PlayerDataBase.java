package ru.vsu.dominoes.db.model;

import java.util.Objects;

public class PlayerDataBase {
  private final String name;
  private Integer win;
  private Integer defeat;
  private Integer id;

  public PlayerDataBase(String name, Integer win, Integer defeat) {
    this.name = name;
    this.win = win;
    this.defeat = defeat;
  }

  public PlayerDataBase(String name, Integer win, Integer defeat, Integer id) {
    this.name = name;
    this.win = win;
    this.defeat = defeat;
    this.id = id;
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

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlayerDataBase that = (PlayerDataBase) o;
    return Objects.equals(name, that.name) &&
            Objects.equals(win, that.win) &&
            Objects.equals(defeat, that.defeat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, win, defeat, id);
  }
}
