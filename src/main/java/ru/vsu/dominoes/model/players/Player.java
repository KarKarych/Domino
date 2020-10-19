package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.model.enums.Sides;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Market;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Player {
  private final String name;
  private final List<Chip> ownChips;
  private Board board;

  public Player(String name) {
    this.name = name;
    this.ownChips = new ArrayList<>(21);
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public String getName() {
    return name;
  }

  public void addChip(Chip chip) {
    ownChips.add(chip);
  }

  public Chip getChip(int index) {
    return ownChips.get(index);
  }

  public int getCountChips() {
    return ownChips.size();
  }

  public Chip getChipFromMarket() {
    Chip chip;
    try {
      chip = board.getMarket().getChip();
      ownChips.add(chip);
    } catch (Market.EmptyMarketException exc) {
      chip = null;
    }
    return chip;
  }

  public List<Chip> getAvailableChips() {
    List<Chip> chips = new ArrayList<>();

    if (board.getLeftChip() != null) {
      for (Chip chip : ownChips) {
        if (!chip.putOn(board, false).equals(Sides.NONE)) {
          chips.add(chip);
        }
      }
    } else {
      chips = ownChips;
    }

    return chips;
  }

  public void addChipOnBoard(Chip chip, Sides side) {
    switch (side) {
      case LEFT -> {
        board.addChipToLeftSide(chip);
        ownChips.remove(chip);
      }
      case RIGHT -> {
        board.addChipToRightSide(chip);
        ownChips.remove(chip);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(name, player.name) &&
            Objects.equals(ownChips, player.ownChips) &&
            Objects.equals(board, player.board);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, ownChips, board);
  }

  @Override
  public String toString() {
    if (ownChips.size() == 0) {
      return "N/A";
    }

    StringBuilder stringBuilder = new StringBuilder();
    for (Chip chip : ownChips) {
      stringBuilder.append(chip);
    }

    return stringBuilder.toString();
  }
}
