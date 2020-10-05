package ru.vsu.dominoes;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private final Table table;

  public Game(int countPlayers) {
    this.table = new Table(countPlayers);
  }

  public Table getTable() {
    return table;
  }

  public int calculateScore(Player player) {
    if (player == null) return 0;
    int score = 0;

    for (int i = 0; i < player.getCountChips(); ++i) {
      Chip chip = player.getChip(i);
      score += chip.getNumber1();
      score += chip.getNumber2();
    }

    return score;
  }

  public List<Player> getWinners() {
    Player winner = null;
    ArrayList<Player> players = new ArrayList<>(table.getPlayers().length);

    for (Player player : table.getPlayers()) {
      if (winner == null || calculateScore(player) < calculateScore(winner)) {
        winner = player;
      }
    }

    players.add(winner);
    for (Player player : table.getPlayers()) {
      if (player != winner && calculateScore(player) == calculateScore(winner)) {
        players.add(player);
      }
    }

    return players;
  }

  public boolean isEnd() {
    boolean isTableEmpty = table.getLeftChip() != null;

    if (isTableEmpty) {
      int left = table.getLeftChip().getNumber1();
      int right = table.getRightChip().getNumber2();
      int playWithLeft = 0;
      int playWithRight = 0;

      for (int i = 0; i < table.getCountChips(); ++i) {
        Chip chip = table.getChip(i);

        if (chip.getNumber1() == left || chip.getNumber2() == left) {
          playWithLeft++;
        }

        if (chip.getNumber1() == right || chip.getNumber2() == right) {
          playWithRight++;
        }
      }

      isTableEmpty = playWithLeft == 7 && playWithRight == 7;
    }

    return isTableEmpty;
  }

  public boolean isPlayerEmpty(Player player) {
    return player.getCountChips() == 0;
  }

  public Moves chooseMove(boolean canPut) {
    Moves move;

    if (canPut) {
      move = Game.Moves.PUT;
    } else {
      if (table.getMarket().getCountChips() == 0) {
        move = Game.Moves.PASS;
      } else {
        move = Game.Moves.GRAB;
      }
    }

    return move;
  }

  public enum Sides {NONE, LEFT, RIGHT}

  public enum Moves {PUT, GRAB, PASS}
}
