package ru.vsu.dominoes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DominoesTest {
  @Test
  public void testChip() {
    Chip chip = new Chip(1, 6);
    assertEquals(1, chip.getNumber1());
    assertEquals(6, chip.getNumber2());
    assertEquals(" [1|6] ", chip.toString());
  }
}
