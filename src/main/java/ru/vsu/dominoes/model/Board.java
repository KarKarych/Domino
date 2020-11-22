package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.players.Player;

import java.util.Deque;
import java.util.LinkedList;

public class Board {
  private final Deque<Chip> chipsOnBoard;
  private final Market market;
  private Player[] players;

  public Board(int countPlayers) {
    this.chipsOnBoard = new LinkedList<>();
    this.market = new Market(countPlayers);
  }

  public Player[] getPlayers() {
    return players;
  }

  public void setPlayers(Player[] players) {
    this.players = players;
  }

  public Market getMarket() {
    return market;
  }

  public void addChipToLeftSide(Chip chip) {
    chipsOnBoard.addFirst(chip);
  }

  public void addChipToRightSide(Chip chip) {
    chipsOnBoard.addLast(chip);
  }

  public Chip getLeftChip() {
    return chipsOnBoard.peekFirst();
  }

  public Chip getRightChip() {
    return chipsOnBoard.peekLast();
  }

  public Deque<Chip> getChips() {
    return chipsOnBoard;
  }
}
