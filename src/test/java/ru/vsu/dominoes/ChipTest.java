package ru.vsu.dominoes;

import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.vsu.dominoes.model.enums.Sides.*;

public class ChipTest {
  private Chip chip;
  private Board board;

  private void initialize() {
    chip = new Chip(1, 6);
    board = new Board(2);
  }

  @Test
  public void testChipGetNumber1() {
    initialize();

    assertEquals(1, chip.getNumber1());
  }

  @Test
  public void testChipGetNumber2() {
    initialize();

    assertEquals(6, chip.getNumber2());
  }

  @Test
  public void testChipPutOn() {
    initialize();

    board.addChipToLeftSide(new Chip(2, 5));

    assertEquals(NONE, chip.putOn(board, true));
    assertEquals(NONE, chip.putOn(board, false));

    board.addChipToRightSide(new Chip(5, 1));

    assertEquals(RIGHT, chip.putOn(board, true));

    chip = new Chip(6, 2);

    assertEquals(LEFT, chip.putOn(board, false));
  }

  @Test
  public void testChipToString() {
    initialize();

    assertEquals(" [1|6] ", chip.toString());
  }
}
