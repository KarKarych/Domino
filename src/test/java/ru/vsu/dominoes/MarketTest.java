package ru.vsu.dominoes;

import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Market;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarketTest {
  private Market market;

  private void initialize() {
    market = new Market(2);
    LinkedList<Chip> chips = new LinkedList<>();
    chips.add(new Chip(1, 4));
    chips.add(new Chip(5, 2));
    chips.add(new Chip(6, 5));
    market.setChips(chips);
  }

  @Test
  public void testMarketGetCountChips() {
    initialize();
    assertEquals(3, market.getCountChips());
  }

  @Test
  public void testMarketGetChip() {
    initialize();
    assertEquals(new Chip(1, 4), market.getChip());
  }
}
