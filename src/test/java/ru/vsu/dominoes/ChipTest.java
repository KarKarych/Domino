package ru.vsu.dominoes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.vsu.dominoes.model.enums.Sides.*;

public class ChipTest {
  Chip chip;
  Board board;

  @BeforeEach
  void initialize() {
    chip = new Chip(1, 6);
    board = new Board(2);
  }

  @Test
  void testChipGetNumber1() {
    assertEquals(1, chip.getNumber1());
  }

  @Test
  void testChipGetNumber2() {
    assertEquals(6, chip.getNumber2());
  }

  @Test
  void testChipPutOn() {
    board.addChipToLeftSide(new Chip(2, 5));

    assertEquals(NONE, chip.putOn(board, true));
    assertEquals(NONE, chip.putOn(board, false));

    board.addChipToRightSide(new Chip(5, 1));

    assertEquals(RIGHT, chip.putOn(board, true));
    assertEquals(LEFT, new Chip(6, 2).putOn(board, false));
  }

  @Test
  void testChipToString() {
    assertEquals(" [1|6] ", chip.toString());
  }
}
