package ru.vsu.dominoes.model.players;

import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Table;
import ru.vsu.dominoes.ui.ConsoleInterface;

import java.util.List;

public class HumanPlayer extends Player {
  public HumanPlayer(String name, Table table) {
    super(name, table);
  }

  public Chip putChip(List<Chip> chips) {
    int i = 0;

    if (chips.size() > 1) {
      do {
        try {
          System.out.print("\nWhich of the chips you can play do you want to put? [1 - " + chips.size() + "]: ");
          i = Integer.parseInt(ConsoleInterface.INPUT.nextLine()) - 1;
        } catch (NumberFormatException exc) {
          i = 404;
        }
      } while (i < 0 || i >= chips.size());
    }

    return chips.get(i);
  }
}
