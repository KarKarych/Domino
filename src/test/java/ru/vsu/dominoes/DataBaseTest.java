package ru.vsu.dominoes;

import org.junit.jupiter.api.Test;
import ru.vsu.dominoes.db.DataBaseDataStorage;
import ru.vsu.dominoes.db.DataStorage;

public class DataBaseTest {

  @Test
  public void testAddUser() {
    DataStorage dataStorage = new DataBaseDataStorage();
    /*List<String> playerNames = Arrays.asList("Kar-Karych", "Unique");
    List<Integer> scores = Arrays.asList(5, 8);
    GameStats gameStats = new GameStats(playerNames, scores);
    dataStorage.saveGame(gameStats);*/

    System.out.println(dataStorage.getLastGames(5));
    /*ArrayList<String> players = new ArrayList<>();
    players.add("Kar-Karych");
    players.add("Unique");
    for (Player player : dataStorage.getPlayers(players)) {
      System.out.println(player);
    }*/
  }
}
