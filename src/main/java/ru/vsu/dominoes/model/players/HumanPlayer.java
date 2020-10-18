package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.ui.StepChooser;

public class HumanPlayer extends Player {

  public HumanPlayer(String name) {
    super(name);
  }

  public Chip putChip(StepChooser stepChooser) {
    return stepChooser.chooseChip();
  }
}
