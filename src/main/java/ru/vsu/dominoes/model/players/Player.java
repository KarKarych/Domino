package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.enums.Sides;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Market;
import ru.vsu.dominoes.model.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
  private final String name;
  private final List<Chip> ownChips;
  private final Table table;

  public Player(String name, Table table) {
    this.name = name;
    this.table = table;
    this.ownChips = new ArrayList<>(21);
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

  public void removeChip(Chip chip) {
    ownChips.remove(chip);
  }

  public Chip getChipFromMarket() {
    Chip chip;
    try {
      chip = table.getMarket().getChip();
      ownChips.add(chip);
    } catch (Market.EmptyMarketException exc) {
      chip = null;
    }
    return chip;
  }

  public List<Chip> getAvailableChips() {
    List<Chip> chips = new ArrayList<>();

    if (table.getLeftChip() != null) {
      for (Chip chip : ownChips) {
        if (!chip.putOn(table, false).equals(Sides.NONE)) {
          chips.add(chip);
        }
      }
    } else {
      chips = ownChips;
    }

    return chips;
  }

  public void addChipOnTable(Chip chip, Sides side) {
    switch (side) {
      case LEFT -> {
        table.addChipToLeftSide(chip);
        ownChips.remove(chip);
      }
      case RIGHT -> {
        table.addChipToRightSide(chip);
        ownChips.remove(chip);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Chip chip : ownChips) {
      stringBuilder.append(chip);
    }

    return stringBuilder.toString();
  }
}
