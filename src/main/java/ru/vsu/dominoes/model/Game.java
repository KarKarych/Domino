package ru.vsu.dominoes.model;

import ru.vsu.dominoes.enums.Moves;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.utils.Names;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {
  private final Table table;

  public Game(int[] countPlayers, String[] namesOfPlayers) {
    this.table = new Table(countPlayers[0] + countPlayers[1]);
    Player[] players = table.getPlayers();
    Market market = table.getMarket();

    List<Integer> names = new LinkedList<>();
    for (int i = 0; i < players.length; ++i) {
      if (i < countPlayers[0]) {
        table.setPlayer(i, new HumanPlayer(namesOfPlayers[i], table));
      } else {
        int index;
        do {
          index = new Random().nextInt(Names.NAMES.length - 1);
        } while (names.contains(index));
        names.add(index);

        table.setPlayer(i, new AIPlayer(Names.NAMES[index], table));
      }
      market.handOutChips(players[i]);
    }
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

  public Moves chooseMove(boolean canPut) {
    Moves move;

    if (canPut) {
      move = Moves.PUT;
    } else {
      if (table.getMarket().getCountChips() == 0) {
        move = Moves.PASS;
      } else {
        move = Moves.GRAB;
      }
    }

    return move;
  }

  public Table getTable() {
    return table;
  }
}
