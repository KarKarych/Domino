package ru.vsu.dominoes.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataStorageTest {
  DataStorage dataStorage;

  @BeforeEach
  void initialize() {
    dataStorage = new DataBaseDataStorage();
  }

  @Test
  public void testSaveAndGetPlayer() {
    String name = "Kar-Karych";
    PlayerDataBase playerTest = new PlayerDataBase(name, 0, 1);
    PlayerDataBase playerSavedTest = dataStorage.savePlayer(playerTest);
    assertEquals(playerSavedTest, dataStorage.getPlayer(name));
  }

  @Test
  public void testSaveAndGetPlayers() {
    String name1 = "Emily";
    String name2 = "Hola";
    PlayerDataBase playerTest1 = new PlayerDataBase(name1, 0, 1);
    PlayerDataBase playerTest2 = new PlayerDataBase(name2, 1, 0);
    List<PlayerDataBase> playersSaved = dataStorage.savePlayers(Arrays.asList(playerTest1, playerTest2));
    assertEquals(playersSaved, dataStorage.getPlayers(Arrays.asList(name1, name2)));
  }

  @Test
  public void testSaveAndGetGame() {
    List<GameStatistic> gameStat = Arrays.asList(new GameStatistic("Emily", 11),
            new GameStatistic("Tom", 18));
    List<GameStatistic> gameStatsSaved = dataStorage.saveGame(gameStat);
    List<GameStatistic> gameStatsReceived = dataStorage.getLastGames(1).get(0);
    Collections.sort(gameStatsSaved);
    Collections.sort(gameStatsReceived);
    assertEquals(gameStatsSaved,  gameStatsReceived);
  }
}
