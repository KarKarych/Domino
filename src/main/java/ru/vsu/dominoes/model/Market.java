package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.players.Player;

import java.util.*;

public class Market {
  private final LinkedList<Chip> chips;
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

  public Market(int countOfPlayers, LinkedList<Chip> chips) {
    this.chips = chips;
    setCountPlayers(countOfPlayers);
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

  public int getCountChips() {
    return chips.size();
  }

  public static final class EmptyMarketException extends RuntimeException {
    public EmptyMarketException() {
      super();
    }
  }
}
