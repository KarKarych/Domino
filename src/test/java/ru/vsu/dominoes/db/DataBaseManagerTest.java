package ru.vsu.dominoes.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.GameTest;
import ru.vsu.dominoes.model.game.Game;
import ru.vsu.dominoes.utils.Pair;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataBaseManagerTest {
  Game game;
  Board board;

  @BeforeEach
  void initialize(){
    GameTest gameTest = new GameTest();

    gameTest.initialize();
    game = gameTest.game;
    board = gameTest.board;
  }

  @Test
  void testSaveResults(){
    DataBaseManager dataBaseManager = new DataBaseManager();

    Pair<List<PlayerDataBase>, List<GameStatistic>> pair = dataBaseManager.saveResults(game, board.getPlayers());

    List<GameStatistic> gameStatsReceived = dataBaseManager.getLastGames(1).get(0);
    List<GameStatistic> gameStatsSaved = pair.second;

    Collections.sort(gameStatsSaved);
    Collections.sort(gameStatsReceived);
    assertEquals(gameStatsSaved, gameStatsReceived);

    List<PlayerDataBase> playersReceived = dataBaseManager.getPlayers(board.getPlayers());
    List<PlayerDataBase> playersSaved = pair.first;

    Collections.sort(playersReceived);
    Collections.sort(playersSaved);

    assertEquals(playersReceived, playersSaved);
  }
}
