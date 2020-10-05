package ru.vsu.dominoes;

import java.util.LinkedList;
import java.util.List;

public class Table {
  private final Player[] players;
  private final List<Chip> chipsOnBoard;
  private final Market market;

  public Table(int countOfPlayers) {
    this.players = new Player[countOfPlayers];
    this.chipsOnBoard = new LinkedList<>();
    this.market = new Market(countOfPlayers);

    addPlayersToTable();
  }

  public Player[] getPlayers() {
    return players;
  }

  public Market getMarket() {
    return market;
  }

  public void addChipToLeftSide(Chip chip) {
    chipsOnBoard.add(0, chip);
  }

  public void addChipToRightSide(Chip chip) {
    chipsOnBoard.add(chipsOnBoard.size(), chip);
  }

  public Chip getLeftChip() {
    if (chipsOnBoard.size() == 0) return null;
    return chipsOnBoard.get(0);
  }

  public Chip getRightChip() {
    if (chipsOnBoard.size() == 0) return null;
    return chipsOnBoard.get(chipsOnBoard.size() - 1);
  }

  public Chip getChip(int position) {
    return chipsOnBoard.get(position);
  }

  public int getCountChips() {
    return chipsOnBoard.size();
  }

  private void addPlayersToTable() {
    String name;
    for (int i = 0; i < players.length; ++i) {
      boolean nameExist;

      do {
        System.out.println("Player " + (i + 1));
        System.out.println("Enter your name: ");
        name = Game.getInput().nextLine().trim();
        nameExist = true;

        int k = i;
        while (nameExist && k > 0) {
          Player player = players[--k];
          if (player.getName().equals(name)) {
            nameExist = false;
          }
        }

        if (!nameExist) {
          System.out.println("This name has already been chosen by another player. Please choose another one.");
        }
      } while (name.isEmpty() || !nameExist);

      players[i] = new Player(name, this);
      market.handOutChips(players[i]);
    }
  }

  @Override
  public String toString() {
    return chipsOnBoard.toString();
  }
}
