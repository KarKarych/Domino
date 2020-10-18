package ru.vsu.dominoes.model;

import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.ui.ConsoleUI;
import ru.vsu.dominoes.ui.GameUI;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private final Board board;
  private final GameUI gameUI;

  public Game(Board board) {
    this.board = board;
    this.gameUI = new ConsoleUI(board);
  }

  public boolean isEnd() {
    boolean isTableEmpty = board.getLeftChip() != null;

    if (isTableEmpty) {
      int left = board.getLeftChip().getNumber1();
      int right = board.getRightChip().getNumber2();
      int playWithLeft = 0;
      int playWithRight = 0;

      for (Chip chip : board.getChips()) {
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

  public static boolean isPlayerEmpty(Player player) {
    return player.getCountChips() == 0;
  }

  public static int calculateScore(Player player) {
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
    List<Player> players = new ArrayList<>(board.getPlayers().length);

    for (Player player : board.getPlayers()) {
      if (winner == null || calculateScore(player) < calculateScore(winner)) {
        winner = player;
      }
    }

    players.add(winner);
    for (Player player : board.getPlayers()) {
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
      if (board.getMarket().getCountChips() == 0) {
        move = Moves.PASS;
      } else {
        move = Moves.GRAB;
      }
    }

    return move;
  }

  public void startGame() {
    int countPlayers = board.getPlayers().length;
    Player player;
    int i = 0;

    do {
      player = board.getPlayers()[i];
      List<Chip> playableChips = player.getAvailableChips();

      gameUI.printBoard(player);

      Moves move = chooseMove(playableChips.size() > 0);
      if (player instanceof HumanPlayer) {
        gameUI.makeMoveHuman((HumanPlayer) player, playableChips, move);
      } else {
        gameUI.makeMoveAI((AIPlayer) player, playableChips, move);
      }

      if (++i > countPlayers - 1) {
        i = 0;
      }
    } while (!isEnd() && !isPlayerEmpty(player));

    gameUI.printResults(getWinners(), player, countPlayers, isEnd());
  }
}
