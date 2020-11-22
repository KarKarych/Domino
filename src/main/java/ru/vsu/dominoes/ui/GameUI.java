package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.db.model.GameStat;
import ru.vsu.dominoes.db.model.PlayerDB;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;

import java.util.List;

public interface GameUI {
  String[] getNamesOfPlayersFromUser(int countPlayers);

  int[] getCountOfPlayersFromUser();

  void printBoard(Player player);

  void printResults(List<Player> winners, Player player, int countPlayers, boolean isEnd);

  Moves chooseMove(Moves move);

  void makeMoveHuman(HumanPlayer player, List<Chip> playableChips, Moves move);

  void makeMoveAI(AIPlayer player, List<Chip> playableChips, Moves move);

  void printResultsOfLastGames(List<List<GameStat>> gameStatsList);

  void printStatsOfPlayers(List<PlayerDB> players);
}
