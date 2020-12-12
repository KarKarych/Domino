package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.GameType;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.utils.GameData;

import java.util.List;

public interface GameUI {
  GameType choiceTypeGame();

  int[] getCountOfPlayersOneDevice();

  String[] getNamesOfPlayersOneDevice(int countPlayers);

  GameData initializeLAN();

  void printFirstPlayer(Player player);

  void printBoard(Player player);

  void printResults(List<Player> winners, Player player, int countPlayers, boolean isEnd);

  Moves chooseMove(Moves move);

  Chip makeMoveHuman(HumanPlayer player, List<Chip> playableChips, Moves move);

  void makeMoveOtherPlayer(HumanPlayer player, Chip chip, List<Chip> playableChips, Moves move);

  void makeMoveAI(AIPlayer player, List<Chip> playableChips, Moves move);

  void printResultsOfLastGames(List<List<GameStatistic>> gameStatsList);

  void printStatsOfPlayers(List<PlayerDataBase> players);
}
