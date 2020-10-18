package ru.vsu.dominoes.model.strategies;

import ru.vsu.dominoes.model.Chip;

import java.util.List;

public class UpStrategy implements Strategy {
  private final List<Chip> chips;

  public UpStrategy(List<Chip> chips) {
    this.chips = chips;
  }

  @Override
  public Chip chooseChip() {
    return chips.get(0);
  }
}
