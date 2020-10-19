package ru.vsu.dominoes;

import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Market;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
  private Player player;

  private void initialize() {
    player = new HumanPlayer("Yuri");
    Player android = new AIPlayer("Werther");
    Board board = new Board(2);
    board.setPlayers(new Player[]{player, android});
    player.setBoard(board);
    Market market = board.getMarket();
    LinkedList<Chip> chips = new LinkedList<>();
    chips.add(new Chip(1, 4));
    chips.add(new Chip(5, 2));
    chips.add(new Chip(6, 5));
    market.setChips(chips);

    player.setBoard(board);
    player.addChip(new Chip(3, 3));
    player.addChip(new Chip(3, 6));
    player.addChip(new Chip(1, 3));

    board.addChipToLeftSide(new Chip(6, 4));
    board.addChipToRightSide(new Chip(4, 1));
  }

  @Test
  public void testPlayerGetChip() {
    initialize();
    assertEquals(new Chip(3, 3), player.getChip(0));
  }

  @Test
  public void testPlayerGetCountChips() {
    initialize();
    assertEquals(3, player.getCountChips());
  }

  @Test
  public void testPlayerGetName() {
    initialize();
    assertEquals("Yuri", player.getName());
  }

  @Test
  public void testPlayerGetAvailableChips() {
    initialize();
    assertEquals(Arrays.asList(new Chip(3, 6), new Chip(1, 3)), player.getAvailableChips());
  }

  @Test
  public void testPlayerGetChipFromMarket() {
    initialize();
    assertEquals(new Chip(1, 4), player.getChipFromMarket());
  }
}
