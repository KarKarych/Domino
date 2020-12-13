package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.players.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Market {
  private LinkedList<Chip> chips;
  private int playerChips;

  public Market(int countOfPlayers) {
    this.chips = new LinkedList<>();

    for (int i = 0; i < 7; ++i) {
      for (int j = i; j < 7; ++j) {
        chips.add(new Chip(i, j));
      }
    }

    Collections.shuffle(chips);

    setCountPlayers(countOfPlayers);
  }

  public int getCountChips() {
    return chips.size();
  }

  public LinkedList<Chip> getChips() {
    return chips;
  }

  public void setChips(LinkedList<Chip> chips) {
    this.chips = chips;
  }

  private void setCountPlayers(int countOfPlayers) {
    switch (countOfPlayers) {
      case 2 -> playerChips = 7;
      case 3, 4 -> playerChips = 5;
      case 5, 6 -> playerChips = 4;
    }
  }

  public void handOutChips(Player player) throws EmptyMarketException {
    try {
      while (player.getCountChips() < playerChips) {
        player.addChip(chips.pop());
      }
    } catch (NoSuchElementException exc) {
      throw new EmptyMarketException();
    }
  }

  public Chip getChip() throws EmptyMarketException {
    try {
      return chips.pop();
    } catch (NoSuchElementException exc) {
      throw new EmptyMarketException();
    }
  }

  public void removeChip(Chip chip) {
    chips.remove(chip);
  }

  public static final class EmptyMarketException extends RuntimeException {
    public EmptyMarketException() {
      super();
    }
  }
}
