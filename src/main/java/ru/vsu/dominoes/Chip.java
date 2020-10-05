package ru.vsu.dominoes;

public class Chip {
  private int number1;
  private int number2;

  public Chip(int number1, int number2) {
    this.number1 = number1;
    this.number2 = number2;
  }

  public int getNumber1() {
    return number1;
  }

  public int getNumber2() {
    return number2;
  }

  public Game.Sides putOn(Table table, boolean turn) {
    Game.Sides side;

    boolean isLeftChipNull = table.getLeftChip() == null;
    int left = isLeftChipNull ? 404 : table.getLeftChip().number1;
    int right = isLeftChipNull ? 404 : table.getRightChip().number2;

    if (isLeftChipNull) {
      side = Game.Sides.LEFT;
    } else {
      boolean isLeftSideChipMatch = left == number1 || left == number2;
      boolean isRightSideChipMatch = right == number1 || right == number2;

      if (isLeftSideChipMatch) {
        side = Game.Sides.LEFT;
        if (turn) {
          turnChip(table, side);
        }
      } else if (isRightSideChipMatch) {
        side = Game.Sides.RIGHT;
        if (turn) {
          turnChip(table, side);
        }
      } else {
        side = Game.Sides.NONE;
      }
    }

    return side;
  }

  public Game.Sides putOn(Table table) {
    return putOn(table, false);
  }

  private void turnChip(Table table, Game.Sides side) {
    int leftSideChip = table.getLeftChip().number1;
    int rightSideChip = table.getRightChip().number2;

    if ((side.equals(Game.Sides.LEFT) && (leftSideChip == number1)) ||
            (side.equals(Game.Sides.RIGHT) && rightSideChip == number2)) {
      int temp = number1;
      number1 = number2;
      number2 = temp;
    }
  }

  @Override
  public String toString() {
    return " [" + number1 + "|" + number2 + "] ";
  }
}
