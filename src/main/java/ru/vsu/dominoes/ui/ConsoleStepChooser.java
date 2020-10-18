package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.model.Chip;

import java.util.List;

public class ConsoleStepChooser implements StepChooser {
  private final List<Chip> chips;

  public ConsoleStepChooser(List<Chip> chips) {
    this.chips = chips;
  }

  @Override
  public Chip chooseChip() {
    int i = 0;
    if (chips.size() > 1) {
      do {
        try {
          System.out.print("\nWhich of the chips you can play do you want to put? [1 - " + chips.size() + "]: ");
          i = Integer.parseInt(ConsoleUI.INPUT.nextLine()) - 1;
        } catch (NumberFormatException exc) {
          i = 404;
        }
      } while (i < 0 || i >= chips.size());
    }

    return chips.get(i);
  }
}
