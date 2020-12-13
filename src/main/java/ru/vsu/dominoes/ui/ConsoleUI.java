package ru.vsu.dominoes.ui;

import ru.vsu.dominoes.db.model.GameStatistic;
import ru.vsu.dominoes.db.model.PlayerDataBase;
import ru.vsu.dominoes.model.Board;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.GameType;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.model.enums.PlayerType;
import ru.vsu.dominoes.model.enums.Sides;
import ru.vsu.dominoes.model.game.Game;
import ru.vsu.dominoes.model.players.AIPlayer;
import ru.vsu.dominoes.model.players.HumanPlayer;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.model.strategies.DownStrategy;
import ru.vsu.dominoes.model.strategies.RandomStrategy;
import ru.vsu.dominoes.model.strategies.Strategy;
import ru.vsu.dominoes.model.strategies.UpStrategy;
import ru.vsu.dominoes.p2p.Host;
import ru.vsu.dominoes.p2p.Peer;
import ru.vsu.dominoes.utils.GameData;
import ru.vsu.dominoes.utils.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements GameUI {
  public static final Scanner INPUT = new Scanner(System.in);
  private Board board;
  private boolean endOfMove;

  public ConsoleUI(Board board, boolean endOfMove) {
    this.endOfMove = endOfMove;
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
  public Chip makeMoveHuman(HumanPlayer player, List<Chip> playableChips, Moves move) {
    System.out.println("\n" + player.getName() + " can play with:\n" + printChips(playableChips));

    Chip chipResult = null;
    switch (chooseMove(move)) {
      case PUT:
        Chip chip = player.putChip(new ConsoleStepChooser(playableChips));
        chipResult = new Chip(chip);
        System.out.println("You put down a chip " + chip + ".");
        player.addChipOnBoard(chip, chip.putOn(board, true));
        break;
      case GRAB:
        chip = player.getChipFromMarket();
        chipResult = new Chip(chip);
        Sides whereCanPut = chip.putOn(board, false);

        System.out.println("You took a chip " + chip + " from the market.");

        if (!whereCanPut.equals(Sides.NONE)) {
          System.out.println("You put down a chip " + chip + ".");
          chip.putOn(board, true);
          player.addChipOnBoard(chip, whereCanPut);
        } else {
          System.out.println("This chip cannot be placed on the board.");
        }
        break;
      case PASS:
        break;
    }

    if (endOfMove) {
      endOfMove();
    }

    return chipResult;
  }

  @Override
  public void makeMoveOtherPlayer(HumanPlayer player, Chip chip, List<Chip> playableChips, Moves move) {
    System.out.println("\n" + player.getName() + " can play with:\n" + printChips(playableChips));

    switch (move) {
      case PUT -> {
        System.out.print(player.getName() + " can put a chip.\n");
        System.out.println(player.getName() + " put down a chip " + chip + ".");
        player.addChipOnBoard(chip, chip.putOn(board, true));
      }
      case GRAB -> {
        System.out.print(player.getName() + " can to pass the turn.\n");
        player.replaceChip(chip);
        Sides whereCanPut = chip.putOn(board, false);
        System.out.println(player.getName() + " took a chip " + chip + " from the market.");
        if (!whereCanPut.equals(Sides.NONE)) {
          System.out.println(player.getName() + " put down a chip " + chip + ".");
          chip.putOn(board, true);
          player.addChipOnBoard(chip, whereCanPut);
        } else {
          System.out.println("This chip cannot be placed on the board.");
        }
      }
      case PASS -> System.out.print(player.getName() + " can take a chip from the market.\n");
    }
  }

  @Override
  public void makeMoveAI(AIPlayer player, List<Chip> playableChips, Moves move, int numberOfStrategy) {
    System.out.println("\n" + player.getName() + " can play with:\n" + printChips(playableChips));

    switch (chooseMove(move)) {
      case PUT:
        Strategy strategy = new RandomStrategy(playableChips);

        switch (numberOfStrategy) {
          case 1 -> strategy = new DownStrategy(playableChips);
          case 2 -> strategy = new UpStrategy(playableChips);
        }

        Chip chosenChip = player.putChip(strategy);
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

    if (endOfMove) {
      endOfMove();
    }
  }

  private void endOfMove() {
    System.out.print("\nPress Enter to go to the next move.");
    String s = INPUT.nextLine();
    if (!s.equals("\n")) {
      INPUT.nextLine();
    }
    System.out.println();
  }

  @Override
  public void printResultsOfLastGames(List<List<GameStatistic>> gameStatsList) {
    System.out.println("\nStatistics of the last " + gameStatsList.size() + " game(s)\n");

    for (List<GameStatistic> gameStatistics : gameStatsList) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Game from ").append(gameStatistics.get(0).getDate(), 11, 16)
              .append(" ")
              .append(gameStatistics.get(0).getDate(), 0, 10)
              .append("\n").
              append("Names");

      int index = 0;
      for (GameStatistic gameStatistic : gameStatistics) {
        if (index++ != 0) {
          stringBuilder.append(", ");
        } else {
          stringBuilder.append(" ");
        }

        stringBuilder.append(gameStatistic.getPlayerName());
      }
      stringBuilder.append(". ").append("Scores");
      index = 0;
      for (GameStatistic gameStatistic : gameStatistics) {
        if (index++ != 0) {
          stringBuilder.append(", ");
        } else {
          stringBuilder.append(" ");
        }

        stringBuilder.append(gameStatistic.getScore());
      }
      System.out.println(stringBuilder.append("\n").toString());
    }
  }

  @Override
  public void printStatsOfPlayers(List<PlayerDataBase> players) {
    System.out.println("\nFull statistics of players who played\n");

    for (PlayerDataBase playerDataBase : players) {
      System.out.println("Player " + playerDataBase.getName() +
              ". Wins " + playerDataBase.getWin() +
              ". Defeats " + playerDataBase.getDefeat());
    }
  }

  private void printBoard() {
    if (board.getChips().size() == 0) {
      System.out.println("N/A");
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      for (Chip chip : board.getChips()) {
        stringBuilder.append(chip);
      }
      System.out.println(stringBuilder.toString());
    }
  }

  @Override
  public void printBoard(Player player) {
    System.out.println("\n* * * * * * * * * * * * BOARD * * * * * * * * * * * *");
    printBoard();
    System.out.println("\nChips in the market: " + board.getMarket().getCountChips());
    System.out.println("\nIt's your turn " + player.getName());
    System.out.println(player);
  }

  @Override
  public void printResults(List<Player> winners, Player player, int countPlayers) {
    System.out.println("\n\n* * * * * * * * * * * * THE GAME IS OVER * * * * * * * * * * * *\n" +
            "BOARD:\n");

    printBoard();

    for (int i = 0; i < countPlayers; ++i) {
      System.out.print("\n" + board.getPlayers()[i].getName()
              + " stayed with " + board.getPlayers()[i].getCountChips() + " chip(s)");

      if (board.getPlayers()[i].getCountChips() != 0) {
        System.out.println(": \n" + board.getPlayers()[i]);
      } else {
        System.out.println();
      }

      System.out.println("\nScore: " + Game.calculateScore(board.getPlayers()[i]));
    }

    System.out.println("\n");

    if (winners.size() == 1) {
      System.out.print(winners.get(0).getName() + " scored the least amount of points.\n\n" +
              "CONGRATULATIONS " + winners.get(0).getName() + ", YOU HAVE WON!!");
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
              "CONGRATULATIONS TO ALL WINNERS!!");
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
  public String[] getNamesOfPlayersOneDevice(int countPlayers) {
    String[] namesOfPlayers = new String[countPlayers];
    for (int i = 0; i < countPlayers; ++i) {
      boolean nameExist;

      do {
        System.out.println("Player " + (i + 1));
        System.out.println("Enter your name: ");
        namesOfPlayers[i] = INPUT.next().trim();
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
  public GameType choiceTypeGame() {
    System.out.println("- * - Welcome to the Domino console game - * -");
    int choice;

    do {
      try {
        System.out.println("\nChoose what you want to do:\n [1] Play\n [2] Exit");
        choice = Integer.parseInt(INPUT.next());
        if (choice != 1 && choice != 2) {
          System.out.println("\nThe selected option must be 1 or 2.");
        }
      } catch (NumberFormatException e) {
        choice = 0;
        System.out.println("\nA valid number was not entered.");
      }
    } while (choice < 1 || choice > 2);

    if (choice == 1) {
      do {
        try {
          System.out.println("\nSelect how you want to play\n [1] Play on this device\n [2] Play LAN Game");
          choice = Integer.parseInt(INPUT.next());
          if (choice != 1 && choice != 2) {
            System.out.println("\nThe selected option must be 1 or 2.");
          }
        } catch (NumberFormatException e) {
          System.out.println("\nA valid number was not entered.");
        }
      } while (choice < 1 || choice > 2);

      return GameType.values()[choice - 1];
    } else {
      System.out.println("\nGoodbye!");
    }

    return null;
  }

  private Pair<String, Integer> inputIP() {
    String ip = null;
    int port = 0;

    try {
      System.out.println("Enter host ip and port (localhost:1000): ");
      String[] answer = INPUT.next().trim().split(":");

      if (answer[0].equals("this")) {
        ip = "localhost";
        port = 1000;
      } else {
        ip = answer[0];
        port = Integer.parseInt(answer[1]);
      }

      while (port < 1 || port > 65535) {
        System.out.println("The port you entered was invalid, please input another port: ");

        answer = INPUT.next().trim().split(":");
        ip = answer[0];
        port = Integer.parseInt(answer[1]);
      }
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      System.out.println("\nA valid number was not entered.");
    }

    return new Pair<>(ip, port);
  }

  @Override
  public GameData initializeLAN() {
    int choice;

    do {
      try {
        System.out.println("\nChoose action:\n [1] Create game\n [2] Join game");
        choice = Integer.parseInt(INPUT.next());
        if (choice != 1 && choice != 2) {
          System.out.println("\nThe selected option must be 1 or 2.");
        }
      } catch (NumberFormatException e) {
        choice = 0;
        System.out.println("\nA valid number was not entered.");
      }
    } while (choice < 1 || choice > 2);

    String[] names = new String[2];
    String ip;
    int port;

    System.out.println("Enter your name: ");
    names[choice - 1] = INPUT.next().trim();

    do {
      Pair<String, Integer> pair = inputIP();

      ip = pair.first;
      port = pair.second;
    } while (ip == null || port == 0);

    try {
      Peer peer;

      if (choice == 1) {
        ServerSocket socket = new Host(ip, port).getSocket();

        while (socket == null) {
          Pair<String, Integer> pair = inputIP();

          ip = pair.first;
          port = pair.second;

          socket = new Host(ip, port).getSocket();
        }

        System.out.println("Waiting for player");

        peer = new Peer(socket.accept());

        System.out.println("Player connected");

        String name = peer.get();

        while (name.equals(names[0])) {
          System.out.println("Hostname matches player name. Player must choose a different name");

          peer.send("1");
          name = peer.get();
        }

        peer.send("0");
        peer.send(names[0]);

        names[1] = name;
      } else {
        Socket socket = new Peer(ip, port).getSocket();

        while (socket == null) {
          Pair<String, Integer> pair = inputIP();

          ip = pair.first;
          port = pair.second;

          socket = new Peer(ip, port).getSocket();
        }

        System.out.println("Connecting to host");

        peer = new Peer(socket);

        System.out.println("Connected to host");

        String name = names[1];

        peer.send(name);

        while (Integer.parseInt(peer.get()) == 1) {
          System.out.println("Hostname matches player name. Enter different name: ");

          name = INPUT.next().trim();
          peer.send(name);
        }

        names[1] = name;
        names[0] = peer.get();
      }

      return new GameData(PlayerType.values()[choice - 1], peer, names);
    } catch (IOException ioException) {
      System.out.println("This address is not available for connection");
    }

    return null;
  }

  @Override
  public void printFirstPlayer(Player player) {
    System.out.println("\nThe first plays " + player.getName() + "\n");
  }

  @Override
  public int[] getCountOfPlayersOneDevice() {
    int[] countPlayers = new int[2];

    do {
      try {
        System.out.print("\nHow many human and ai players will play? [2-6 total]: ");
        String tempPlayer1 = INPUT.next();
        String tempPlayer2 = INPUT.next();
        countPlayers[0] = Integer.parseInt(tempPlayer1);
        countPlayers[1] = Integer.parseInt(tempPlayer2);

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
}
