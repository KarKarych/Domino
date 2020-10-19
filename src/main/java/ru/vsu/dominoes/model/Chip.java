package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.enums.Sides;

import java.util.Objects;

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

  public Sides putOn(Board board, boolean isTurnNeed) {
    Sides side;

    boolean isLeftChipNull = board.getLeftChip() == null;
    int left = isLeftChipNull ? 404 : board.getLeftChip().number1;
    int right = isLeftChipNull ? 404 : board.getRightChip().number2;

    if (isLeftChipNull) {
      side = Sides.LEFT;
    } else {
      boolean isLeftSideChipMatch = left == number1 || left == number2;
      boolean isRightSideChipMatch = right == number1 || right == number2;

      if (isLeftSideChipMatch) {
        side = Sides.LEFT;
        if (isTurnNeed) {
          turnChip(board, side);
        }
      } else if (isRightSideChipMatch) {
        side = Sides.RIGHT;
        if (isTurnNeed) {
          turnChip(board, side);
        }
      } else {
        side = Sides.NONE;
      }
    }

    return side;
  }

  private void turnChip(Board board, Sides side) {
    int leftSideChip = board.getLeftChip().number1;
    int rightSideChip = board.getRightChip().number2;

    if ((side.equals(Sides.LEFT) && (leftSideChip == number1)) ||
            (side.equals(Sides.RIGHT) && rightSideChip == number2)) {
      int temp = number1;
      number1 = number2;
      number2 = temp;
    }
  }

  @Override
  public String toString() {
    return " [" + number1 + "|" + number2 + "] ";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Chip chip = (Chip) o;
    return number1 == chip.number1 &&
            number2 == chip.number2;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number1, number2);
  }
}
