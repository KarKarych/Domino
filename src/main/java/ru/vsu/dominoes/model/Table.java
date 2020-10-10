package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.players.Player;

import java.util.LinkedList;
import java.util.List;

public class Table {
  private final Player[] players;
  private final List<Chip> chipsOnBoard;
  private final Market market;

  public Table(int countPlayers) {
    this.players = new Player[countPlayers];
    this.chipsOnBoard = new LinkedList<>();
    this.market = new Market(countPlayers);
  }

  public void setPlayer(int index, Player player) {
    if (index >= 0 && index < players.length) {
      players[index] = player;
    }
  }

  public Player[] getPlayers() {
    return players;
  }

  public Market getMarket() {
    return market;
  }

  public void addChipToLeftSide(Chip chip) {
    chipsOnBoard.add(0, chip);
  }

  public void addChipToRightSide(Chip chip) {
    chipsOnBoard.add(chipsOnBoard.size(), chip);
  }

  public Chip getLeftChip() {
    if (chipsOnBoard.size() == 0) return null;
    return chipsOnBoard.get(0);
  }

  public Chip getRightChip() {
    if (chipsOnBoard.size() == 0) return null;
    return chipsOnBoard.get(chipsOnBoard.size() - 1);
  }

  public Chip getChip(int position) {
    return chipsOnBoard.get(position);
  }

  public int getCountChips() {
    return chipsOnBoard.size();
  }

  @Override
  public String toString() {
    return chipsOnBoard.toString();
  }
}
