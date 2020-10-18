package ru.vsu.dominoes.model.strategies;

import ru.vsu.dominoes.model.Chip;

import java.util.List;

public class DownStrategy implements Strategy {
  private final List<Chip> chips;

  public DownStrategy(List<Chip> chips) {
    this.chips = chips;
  }

  @Override
  public Chip chooseChip() {
    if (chips.size() == 1) {
      return chips.get(0);
    }

    return chips.get(chips.size() - 1);
  }
}
