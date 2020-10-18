package ru.vsu.dominoes.model.strategies;

import ru.vsu.dominoes.model.Chip;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {
  private final List<Chip> chips;

  public RandomStrategy(List<Chip> chips) {
    this.chips = chips;
  }

  @Override
  public Chip chooseChip() {
    if (chips.size() == 1) {
      return chips.get(0);
    }

    return chips.get(new Random().nextInt(chips.size() - 1));
  }
}
