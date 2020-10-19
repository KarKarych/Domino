package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.Game;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.enums.Sides;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.model.strategies.RandomStrategy;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements GameUI {
  public static final Scanner INPUT = new Scanner(System.in);
  private Board board;

  public ConsoleUI(Board board) {
    this.board = board;
  }

  public ConsoleUI() {

  }

  private String printChips(List<Chip> chips) {
    StringBuilder stringBuilder = new StringBuilder();
    if (chips.size() == 0) {
      stringBuilder.append("N/A\n");
    } else {
      for (Chip chip : chips) {
        stringBuilder.append(chip);
      }
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }

  @Override
  public void makeMoveHuman(HumanPlayer player, List<Chip> playableChips, Moves move) {
    System.out.println("\nYou can play with:\n" + printChips(playableChips));

    switch (chooseMove(move)) {
      case PUT:
        Chip chosenChip = player.putChip(new ConsoleStepChooser(playableChips));
        System.out.println("You put down a chip " + chosenChip + ".");
        player.addChipOnBoard(chosenChip, chosenChip.putOn(board, true));
        break;
      case GRAB:
        Chip chipFromMarket = player.getChipFromMarket();
        Sides whereCanPut = chipFromMarket.putOn(board, false);

        System.out.println("You took a chip " + chipFromMarket + " from the market.");

        if (!whereCanPut.equals(Sides.NONE)) {
          System.out.println("You put down a chip " + chipFromMarket + ".");
          chipFromMarket.putOn(board, true);
          player.addChipOnBoard(chipFromMarket, whereCanPut);
        } else {
          System.out.println("This chip cannot be placed on the board.");
        }
        break;
      case PASS:
        break;
    }

    endOfMove();
  }

  @Override
  public void makeMoveAI(AIPlayer player, List<Chip> playableChips, Moves move) {
    System.out.println("\n" + player.getName() + " can play with:\n" + printChips(playableChips));

    switch (chooseMove(move)) {
      case PUT:
        Chip chosenChip = player.putChip(new RandomStrategy(playableChips));
        System.out.println(player.getName() + " put down a chip " + chosenChip + ".");
        player.addChipOnBoard(chosenChip, chosenChip.putOn(board, true));
        break;
      case GRAB:
        Chip chipFromMarket = player.getChipFromMarket();
        Sides whereCanPut = chipFromMarket.putOn(board, false);

        System.out.println(player.getName() + " took a chip " + chipFromMarket + " from the market.");

        if (!whereCanPut.equals(Sides.NONE)) {
          System.out.println(player.getName() + " put down a chip " + chipFromMarket + ".");
          chipFromMarket.putOn(board, true);
          player.addChipOnBoard(chipFromMarket, whereCanPut);
        } else {
          System.out.println("This chip cannot be placed on the board.");
        }
        break;
      case PASS:
        break;
    }

    endOfMove();
  }

  private void endOfMove() {
    System.out.print("\nPress Enter to go to the next move.");
    INPUT.nextLine();
    System.out.println();
  }

  @Override
  public void printBoard(Player player) {
    System.out.println("* * * * * * * * * * * * BOARD * * * * * * * * * * * *");
    System.out.println(board);
    System.out.println("\nChips in the market: " + board.getMarket().getCountChips());
    System.out.println("\nIt's your turn " + player.getName());
    System.out.println(player);
  }

  @Override
  public void printResults(List<Player> winners, Player player, int countPlayers, boolean isEnd) {
    int[] scores = new int[countPlayers];
    System.out.println("\n\n* * * * * * * * * * * * THE GAME IS OVER * * * * * * * * * * * *\n" +
            "BOARD:\n" + board);
    for (int i = 0; i < countPlayers; ++i) {
      int n = board.getPlayers()[i].getCountChips();
      if (n > 0) {
        System.out.println("\n" + board.getPlayers()[i].getName()
                + " stayed with " + n
                + " chip(s): \n" + board.getPlayers()[i]);
      }

      if (!Game.isPlayerEmpty(player) && isEnd) {
        scores[i] = Game.calculateScore(board.getPlayers()[i]);
        System.out.println("Score: " + scores[i]);
      }
    }

    if (Game.isPlayerEmpty(player)) {
      System.out.println("\n" + player.getName() + " won.\n\nCONGRATULATIONS, YOU HAVE WON!!");
    } else {
      System.out.println("\n");

      if (winners.size() == 1) {
        System.out.print(winners.get(0).getName() + " scored the least amount of points.\n\n" +
                "CONGRATULATIONS, YOU HAVE WON!!");
      } else {
        for (int k = 0; k < winners.size(); ++k) {
          System.out.print(winners.get(k).getName());
          if (k == winners.size() - 2) {
            System.out.print(" and ");
          } else {
            if (k < winners.size() - 2) {
              System.out.print(", ");
            }
          }
        }
        System.out.println(" scored the lowest number of points.\n" +
                "CONGRATULATIONS TO ALL!!");
      }
    }

    System.out.println("\n\nThank you for playing!\n");
  }

  @Override
  public Moves chooseMove(Moves move) {
    System.out.print("You can ");
    switch (move) {
      case PUT -> System.out.print("put a chip.\n");
      case GRAB -> System.out.print("to pass the turn.\n");
      case PASS -> System.out.print("take a chip from the market.\n");
    }
    return move;
  }

  @Override
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

  @Override
  public int[] getCountOfPlayersFromUser() {
    int choice = 0;

    System.out.println("- * - Welcome to the Domino console game - * -");

    do {
      try {
        System.out.println("\nChoose what you want to do:\n [1] Play\n [2] Exit");
        choice = Integer.parseInt(INPUT.nextLine());
        if (choice != 1 && choice != 2) {
          System.out.println("\nThe selected option must be 1 or 2.");
        }
      } catch (NumberFormatException e) {
        System.out.println("\nA valid number was not entered.");
      }
    } while (choice < 1 || choice > 2);

    if (choice == 1) {
      int[] countPlayers = new int[2];
      do {
        try {
          System.out.print("\nHow many human and ai players will play? [2-6 total]: ");
          String[] tempPlayers = INPUT.nextLine().split("[ ,]");
          countPlayers[0] = Integer.parseInt(tempPlayers[0]);
          countPlayers[1] = Integer.parseInt(tempPlayers[1]);
          if ((countPlayers[0] >= 2 && countPlayers[1] <= 2 ||
                  countPlayers[0] <= 2 && countPlayers[1] >= 2 ||
                  countPlayers[0] >= 1 && countPlayers[1] >= 1) &&
                  countPlayers[0] + countPlayers[1] <= 6) {
            break;
          }
        } catch (Exception e) {
          System.out.println("A valid number was not entered.");
        }
      } while (true);
      return countPlayers;
    }

    System.out.println("\nGoodbye!");
    return null;
  }
}
