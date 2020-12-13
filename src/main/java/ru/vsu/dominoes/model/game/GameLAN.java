package ru.vsu.dominoes.model.game;

import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.enums.PlayerType;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.p2p.Peer;
import ru.vsu.dominoes.parser.JsonParser;
import ru.vsu.dominoes.ui.GameUI;
import ru.vsu.dominoes.utils.Pair;

import java.util.List;
import java.util.Random;

public class GameLAN extends Game {
  private final Peer peer;
  private final PlayerType playerApp;

  public GameLAN(Board board, GameUI gameUI, int strategy, boolean random, Peer peer, PlayerType playerApp) {
    super(board, gameUI, strategy, random);

    this.peer = peer;
    this.playerApp = playerApp;
  }

  @Override
  public void play() {
    int countPlayers = board.getPlayers().length;
    int turn = 0;

    if (random) {
      if (playerApp.equals(PlayerType.HOST)) {
        turn = new Random().nextInt(countPlayers);
        peer.send(String.valueOf(turn));
      } else {
        turn = Integer.parseInt(peer.get());
      }
    }

    PlayerType currentPlayer = PlayerType.values()[turn];
    Player player = board.getPlayers()[turn];
    gameUI.printFirstPlayer(player);

    do {
      player = board.getPlayers()[turn];
      List<Chip> playableChips = player.getAvailableChips();
      gameUI.printBoard(player);

      JsonParser jsonParser = new JsonParser();
      if (playerApp.equals(currentPlayer)) {
        Moves move = chooseMove(playableChips.size() > 0);
        Chip chip = gameUI.makeMoveHuman((HumanPlayer) player, playableChips, move);

        if (chip != null) {
          peer.send(jsonParser.parseMoveAndChipJson(move, chip));
        } else {
          peer.send("pass");
        }
      } else {
        Pair<Chip, Moves> pair = jsonParser.parseMoveAndChipObjects(peer.get());

        if (pair != null) {
          gameUI.makeMoveOtherPlayer((HumanPlayer) player, pair.first, playableChips, pair.second);
        }
      }

      if (++turn > countPlayers - 1) {
        turn = 0;
      }

      currentPlayer = currentPlayer.equals(PlayerType.HOST) ? PlayerType.PLAYER : PlayerType.HOST;
    } while (isEnd() && isPlayerEmpty(player));

    gameUI.printResults(getWinners(), player, countPlayers);
  }
}
