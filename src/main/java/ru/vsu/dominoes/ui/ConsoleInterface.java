package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.enums.Moves;
import ru.vsu.dominoes.enums.Sides;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Game;
import ru.vsu.dominoes.model.Player;
import ru.vsu.dominoes.model.Table;

import java.util.List;
import java.util.Scanner;

public class ConsoleInterface {
  private static final Scanner INPUT = new Scanner(System.in);
  private final Game game;
  private Table table;

  public ConsoleInterface() {
    int countPlayers = getCountOfPlayersFromUser();
    String[] namesOfPlayers = getNamesOfPlayersFromUser(countPlayers);
    this.game = new Game(countPlayers, namesOfPlayers);

    if (countPlayers != 0) {
      this.table = game.getTable();
      play();
    }
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

  private Moves chooseMove(boolean canPut) {
    Moves move = game.chooseMove(canPut);

    System.out.print("You can ");
    switch (move) {
      case PUT -> System.out.print("put a chip.\n");
      case GRAB -> System.out.print("to pass the turn.\n");
      case PASS -> System.out.print("take a chip from the market.\n");
    }
    return move;
  }

  private void play() {
    int countPlayers = table.getPlayers().length;
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
    } while (!game.isEnd() && !game.isPlayerEmpty(player));


    System.out.println("\n\n* * * THE GAME IS OVER * * *\nTABLE:\n" + table);
    for (i = 0; i < countPlayers; ++i) {
      int n = table.getPlayers()[i].getCountChips();
      if (n > 0) {
        System.out.println("\n" + table.getPlayers()[i].getName()
                + " stayed with " + n
                + " chip(s): \n" + table.getPlayers()[i]);
      }

      if (!game.isPlayerEmpty(player) && game.isEnd()) {
        scores[i] = game.calculateScore(table.getPlayers()[i]);
        System.out.println("Score: " + scores[i]);
      }
    }

    if (game.isPlayerEmpty(player)) {
      System.out.println("\n" + player.getName() + " won.\n\nCONGRATULATIONS, YOU HAVE WON!!");
    } else {
      System.out.println("\n");
      List<Player> winners = game.getWinners();

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

  public String[] getNamesOfPlayersFromUser(int countPlayers) {
    String[] namesOfPlayers = new String[countPlayers];
    for (int i = 0; i < countPlayers; ++i) {
      boolean nameExist;

      do {
        System.out.println("Player " + (i + 1));
        System.out.println("Enter your name: ");
        namesOfPlayers[i] = INPUT.nextLine().trim();
        nameExist = true;

        for (int k = i - 1; k >= 0; k--) {
          if (namesOfPlayers[k].equals(namesOfPlayers[i])) {
            nameExist = false;
            break;
          }
        }

        if (!nameExist) {
          System.out.println("This name has already been chosen by another player. Please choose another one.");
        }
      } while (namesOfPlayers[i].isEmpty() || !nameExist);

    }

    return namesOfPlayers;
  }

  public int getCountOfPlayersFromUser() {
    int choice = 0;

    System.out.println("- * - Welcome to the Domino console game - * -");

    do {
      try {
        System.out.println("\nChoose what you want to do:\n[1] Play\n[2] Exit");
        choice = Integer.parseInt(INPUT.nextLine());
        if (choice != 1 && choice != 2) {
          System.out.println("\nThe selected option must be 1 or 2.");
        }
      } catch (NumberFormatException e) {
        System.out.println("\nA valid number was not entered.");
      }
    } while (choice < 1 || choice > 2);

    if (choice == 1) {
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

      return countPlayers;
    }

    System.out.println("\nGoodbye!");
    return 0;
  }
}
