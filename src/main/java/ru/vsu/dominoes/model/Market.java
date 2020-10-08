package ru.vsu.dominoes.model;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

public class Market {
  private final Stack<Chip> chips;
  private int playerChips;

  public Market(int countOfPlayers) {
    this.chips = new Stack<>();

    for (int i = 0; i < 7; ++i) {
      for (int j = i; j < 7; ++j) {
        chips.add(new Chip(i, j));
      }
    }

    Collections.shuffle(chips);

    switch (countOfPlayers) {
      case 2 -> playerChips = 7;
      case 3, 4 -> playerChips = 5;
    }
  }

  public void handOutChips(Player player) throws EmptyMarketException {
    try {
      while (player.getCountChips() < playerChips) {
        player.addChip(chips.pop());
      }
    } catch (EmptyStackException exc) {
      throw new EmptyMarketException();
    }
  }

  public Chip getChip() throws EmptyMarketException {
    try {
      return chips.pop();
    } catch (EmptyStackException exc) {
      throw new EmptyMarketException();
    }
  }

  public int getCountChips() {
    return chips.size();
  }

  public static final class EmptyMarketException extends RuntimeException {
    public EmptyMarketException() {
      super();
    }
  }
}
