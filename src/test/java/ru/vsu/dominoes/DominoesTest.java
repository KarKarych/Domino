package ru.vsu.dominoes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class DominoesTest {
  Player player;
  Table table;
  Market market;
  Chip chip;
  Game game;

  @Test
  public void testChip() {
    chip = new Chip(1, 6);
    assertEquals(1, chip.getNumber1());
    assertEquals(6, chip.getNumber2());
    assertEquals(" [1|6] ", chip.toString());
  }

  @Test
  public void testMarket() {
    market = new Market(2);
    assertEquals(28, market.getCountChips());
  }

  @Test
  public void testPlayer() {
    player = new Player("Vasya", new Table(2));
    chip = new Chip(1, 6);
    player.addChip(chip);
    List<Chip> chips = new ArrayList<>();
    chips.add(chip);
    assertEquals(chip, player.getChip(0));
    assertEquals(chips, player.getAvailableChips());
    assertEquals(1, player.getCountChips());
    assertEquals("Vasya", player.getName());
  }

  @Test
  public void testTable() {
    table = new Table(2);
    table.setPlayer(0, player);
    assertEquals(player, table.getPlayers()[0]);
  }
}
