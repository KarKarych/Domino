package ru.vsu.dominoes.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.model.game.Game;
import ru.vsu.dominoes.model.game.GameOneDevice;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.ui.TestUI;
import ru.vsu.dominoes.utils.Names;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
  public Game game;
  public Board board;

  @BeforeEach
  public void initialize() {
    Player[] players = new AIPlayer[5];

    for (int i = 0; i < players.length; i++) {
      players[i] = new AIPlayer(Names.NAMES[i]);
    }

    board = new Board(players.length);
    board.setPlayers(players);
    Market market = board.getMarket();

    LinkedList<Chip> chips = new LinkedList<>();

    for (int i = 0; i < 7; ++i) {
      for (int j = i; j < 7; ++j) {
        chips.add(new Chip(i, j));
      }
    }

    market.setChips(chips);

    for (Player player : players) {
      player.setBoard(board);
      market.handOutChips(player);
    }

    game = new GameOneDevice(board, new TestUI(board), 1, false);
    game.play();
  }

  @Test
  void testPlay() {
    assertEquals(9, Game.calculateScore(board.getPlayers()[0]));
    assertEquals(27, Game.calculateScore(board.getPlayers()[1]));
    assertEquals(0, Game.calculateScore(board.getPlayers()[2]));
    assertEquals(15, Game.calculateScore(board.getPlayers()[3]));
    assertEquals(20, Game.calculateScore(board.getPlayers()[4]));
  }
}
