package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Table;

import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {
  public AIPlayer(String name, Table table) {
    super(name, table);
  }

  public Chip putChip(List<Chip> chips) {
    if (chips.size() == 1) {
      return chips.get(0);
    }

    return chips.get(new Random().nextInt(chips.size() - 1));
  }
}
