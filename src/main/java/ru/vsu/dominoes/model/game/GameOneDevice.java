package ru.vsu.dominoes.model.game;

import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.ui.GameUI;

import java.util.List;
import java.util.Random;

public class GameOneDevice extends Game {
  public GameOneDevice(Board board, GameUI gameUI) {
    super(board, gameUI);
  }

  @Override
  public void play() {
    int countPlayers = board.getPlayers().length;
    int turn = new Random().nextInt(countPlayers);
    Player player = board.getPlayers()[turn];

    gameUI.printFirstPlayer(player);

    do {
      player = board.getPlayers()[turn];

      List<Chip> playableChips = player.getAvailableChips();

      gameUI.printBoard(player);

      Moves move = chooseMove(playableChips.size() > 0);
      if (player instanceof HumanPlayer) {
        gameUI.makeMoveHuman((HumanPlayer) player, playableChips, move);
      } else {
        gameUI.makeMoveAI((AIPlayer) player, playableChips, move);
      }

      if (++turn > countPlayers - 1) {
        turn = 0;
      }
    } while (!isEnd() && !isPlayerEmpty(player));

    gameUI.printResults(getWinners(), player, countPlayers, isEnd());
  }
}
