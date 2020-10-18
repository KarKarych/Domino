package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.model.strategies.Strategy;
import ru.vsu.dominoes.model.Chip;

public class AIPlayer extends Player {

  public AIPlayer(String name) {
    super(name);
  }

  public Chip putChip(Strategy strategy) {
    return strategy.chooseChip();
  }
}
