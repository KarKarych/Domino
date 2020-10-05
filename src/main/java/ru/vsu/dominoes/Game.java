package ru.vsu.dominoes;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
  public enum Sides {NONE, LEFT, RIGHT}

  private enum Moves {PUT, GRAB, PASS}

  private static final Scanner INPUT = new Scanner(System.in);

  private Table table;

  public static Scanner getInput() {
    return INPUT;
  }

  Game() {
    startGame();
  }

  private void introducePlayers() {
    int countPlayers = 0;
    do {
      try {
        System.out.print("How many players will play? [2-4]: ");
        countPlayers = Integer.parseInt(INPUT.nextLine());
        if (countPlayers < 2 || countPlayers > 4) {
          System.out.println("The number of players must be from 2 to 4.");
        }
      } catch (NumberFormatException e) {
        System.out.println("A valid number was not entered.");
      }
    } while (countPlayers < 2 || countPlayers > 4);

    table = new Table(countPlayers);
    int[] scores = new int[countPlayers];

    int i = 0;
    List<Chip> playableChips;
    Player player;
    do {
      player = table.getPlayers()[i];
      playableChips = player.getAvailableChips();

      System.out.println("* * * * * * * * * * * * TABLE * * * * * * * * * * * *");
      System.out.println(table);
      System.out.println("\nChips in the market: " + table.getMarket().getCountChips());
      System.out.println("\nIt's your turn " + player.getName());
      System.out.println(player);
      System.out.println("\nYou can play with:\n" + playableChips + "\n");

      switch (chooseMove(playableChips.size() > 0)) {
        case PUT:
          Chip chosenChip = putChip(playableChips);
          System.out.println("You put down a chip " + chosenChip + ".");
          player.addChipOnTable(chosenChip, chosenChip.putOn(table, true));
          break;
        case GRAB:
          Chip chipFromMarket = player.getChipFromMarket();
          Sides whereCanPut = chipFromMarket.putOn(table, false);

          System.out.println("You took a chip " + chipFromMarket + " from the market.");

          if (!whereCanPut.equals(Sides.NONE)) {
            System.out.println("You put down a chip " + chipFromMarket + ".");
            chipFromMarket.putOn(table, true);
            player.addChipOnTable(chipFromMarket, whereCanPut);
          } else {
            System.out.println("This chip cannot be placed on the table.");
          }
          break;
        case PASS:
          break;
      }
      System.out.print("\nPress Enter to go to the next move.");
      INPUT.nextLine();
      System.out.println();

      if (++i > countPlayers - 1) {
        i = 0;
      }
    } while (!isEnd() && !isPlayerEmpty(player));


    System.out.println("\n\n* * * THE GAME IS OVER * * *\nTABLE:\n" + table);
    for (i = 0; i < countPlayers; ++i) {
      int n = table.getPlayers()[i].getCountChips();
      if (n > 0) {
        System.out.println("\n" + table.getPlayers()[i].getName()
                + " stayed with " + n
                + " chip(s): \n" + table.getPlayers()[i]);
      }

      if (!isPlayerEmpty(player) && isEnd()) {
        scores[i] = calculateScore(table.getPlayers()[i]);
        System.out.println("Score: " + scores[i]);
      }
    }

    if (isPlayerEmpty(player)) {
      System.out.println("\n" + player.getName() + " won.\n\nCONGRATULATIONS, YOU HAVE WON!!");
    } else {
      System.out.println("\n");
      List<Player> winners = getWinners();

      if (winners.size() == 1) {
        System.out.print(winners.get(0).getName() + " scored the least amount of points.\n\nCONGRATULATIONS, YOU HAVE WON!!");
      } else {
        for (int k = 0; k < winners.size(); ++k) {
          System.out.print(winners.get(k).getName());
          if (k == winners.size() - 2) {
            System.out.println(" and ");
          } else {
            if (k < winners.size() - 2) {
              System.out.println(", ");
            }
          }
        }
        System.out.println(" scored the lowest number of points.\n\nCONGRATULATIONS TO ALL!!");
      }
    }

    System.out.println("\n\nThank you for playing!\n");
  }

  private Moves chooseMove(boolean canPut) {
    Moves move;

    System.out.print("You can ");

    if (canPut) {
      System.out.print("put a chip.");
      move = Moves.PUT;
    } else {
      if (table.getMarket().getCountChips() == 0) {
        System.out.print("to pass the turn.");
        move = Moves.PASS;
      } else {
        System.out.print("take a chip from the market.");
        move = Moves.GRAB;
      }
    }
    System.out.println();

    return move;
  }

  private static Chip putChip(List<Chip> chips) {
    int i = 0;

    if (chips.size() > 1) {
      do {
        try {
          System.out.print("\nWhich of the chips you can play do you want to put? [1 - " + chips.size() + "]: ");
          i = Integer.parseInt(INPUT.nextLine()) - 1;
        } catch (NumberFormatException exc) {
          i = 404;
        }
      } while (i < 0 || i >= chips.size());
    }

    return chips.get(i);
  }

  private boolean isEnd() {
    boolean isTableEmpty = table.getLeftChip() != null;

    if (isTableEmpty) {
      int left = table.getLeftChip().getNumber1();
      int right = table.getRightChip().getNumber2();
      int playWithLeft = 0;
      int playWithRight = 0;

      for (int i = 0; i < table.getCountChips(); ++i) {
        Chip chip = table.getChip(i);

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

  private boolean isPlayerEmpty(Player player) {
    return player.getCountChips() == 0;
  }

  private static int calculateScore(Player player) {
    if (player == null) return 0;
    int score = 0;

    for (int i = 0; i < player.getCountChips(); ++i) {
      Chip chip = player.getChip(i);
      score += chip.getNumber1();
      score += chip.getNumber2();
    }

    return score;
  }

  private List<Player> getWinners() {
    Player winner = null;
    ArrayList<Player> players = new ArrayList<>(table.getPlayers().length);

    for (Player player : table.getPlayers()) {
      if (winner == null || calculateScore(player) < calculateScore(winner)) {
        winner = player;
      }
    }

    players.add(winner);
    for (Player player : table.getPlayers()) {
      if (player != winner && calculateScore(player) == calculateScore(winner)) {
        players.add(player);
      }
    }

    return players;
  }

  public void startGame() {
    int op = 0;

    System.out.println("- * - Welcome to the Domino console game - * -");

    do {
      try {
        System.out.println("\nChoose what you want to do:\n[1] Play\n[2] Exit");
        op = Integer.parseInt(INPUT.nextLine());
        if (op != 1 && op != 2) {
          System.out.println("\nThe selected option must be 1 or 2.");
        }
      } catch (NumberFormatException e) {
        System.out.println("\nA valid number was not entered.");
      }
    } while (op < 1 || op > 2);

    if (op == 1) {
      introducePlayers();
    }

    System.out.println("\nGoodbye!");
  }
}
