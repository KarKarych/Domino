package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.GameType;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.enums.Sides;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.model.strategies.DownStrategy;
import ru.vsu.dominoes.model.strategies.RandomStrategy;
import ru.vsu.dominoes.model.strategies.Strategy;
import ru.vsu.dominoes.model.strategies.UpStrategy;
import ru.vsu.dominoes.utils.GameData;

import java.util.List;

public class WithoutUI implements GameUI {
  private final Board board;

  public WithoutUI(Board board) {
    this.board = board;
  }

  @Override
  public void makeMoveAI(AIPlayer player, List<Chip> playableChips, Moves move, int numberOfStrategy) {
    switch (move) {
      case PUT:
        Strategy strategy = new RandomStrategy(playableChips);

        switch (numberOfStrategy) {
          case 1 -> strategy = new DownStrategy(playableChips);
          case 2 -> strategy = new UpStrategy(playableChips);
        }

        Chip chosenChip = player.putChip(strategy);

        player.addChipOnBoard(chosenChip, chosenChip.putOn(board, true));
        break;
      case GRAB:
        Chip chipFromMarket = player.getChipFromMarket();
        Sides whereCanPut = chipFromMarket.putOn(board, false);

        if (!whereCanPut.equals(Sides.NONE)) {
          chipFromMarket.putOn(board, true);
          player.addChipOnBoard(chipFromMarket, whereCanPut);
        }
        break;
      case PASS:
        break;
    }
  }

  @Override
  public Chip makeMoveHuman(HumanPlayer player, List<Chip> playableChips, Moves move) {
    return null;
  }

  @Override
  public void makeMoveOtherPlayer(HumanPlayer player, Chip chip, List<Chip> playableChips, Moves move) {
  }

  @Override
  public void printResultsOfLastGames(List<List<GameStatistic>> gameStatsList) {
  }

  @Override
  public void printStatsOfPlayers(List<PlayerDataBase> players) {
  }

  @Override
  public void printBoard(Player player) {
  }

  @Override
  public void printResults(List<Player> winners, Player player, int countPlayers) {
  }

  @Override
  public Moves chooseMove(Moves move) {
    return null;
  }

  @Override
  public String[] getNamesOfPlayersOneDevice(int countPlayers) {
    return null;
  }

  @Override
  public GameType choiceTypeGame() {
    return null;
  }

  @Override
  public GameData initializeLAN() {
    return null;
  }

  @Override
  public void printFirstPlayer(Player player) {
  }

  @Override
  public int[] getCountOfPlayersOneDevice() {
    return null;
  }
}
