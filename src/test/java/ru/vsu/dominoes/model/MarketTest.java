package ru.vsu.dominoes.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarketTest {
  Market market;

  @BeforeEach
  void initialize() {
    market = new Market(2);
    LinkedList<Chip> chips = new LinkedList<>();
    chips.add(new Chip(1, 4));
    chips.add(new Chip(5, 2));
    chips.add(new Chip(6, 5));
    market.setChips(chips);
  }

  @Test
  void testGetCountChips() {
    assertEquals(3, market.getCountChips());
  }

  @Test
  void testGetChip() {
    assertEquals(new Chip(1, 4), market.getChip());
  }
}
