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
        System.out.print("Сколько игроков будут играть? [2-4]: ");
        countPlayers = Integer.parseInt(INPUT.nextLine());
        if (countPlayers < 2 || countPlayers > 4) {
          System.out.println("Количество игроков должно быть от 2 до 4.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Действительный номер не введён.");
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

      System.out.println("* * * СТОЛ * * *");
      System.out.println(table);
      System.out.println("\nФишки в куче: " + table.getMarket().getCountChips());
      System.out.println("\nНастала очередь " + player.getName());
      System.out.println(player);
      System.out.println("\nВы можете играть с:\n" + playableChips + "\n");

      switch (chooseMove(playableChips.size() > 0)) {
        case PUT:
          Chip chosenChip = putChip(playableChips);
          System.out.println("Вы положили фишку " + chosenChip + ".");
          player.addChipOnTable(chosenChip, chosenChip.putOn(table, true));
          break;
        case GRAB:
          Chip chipFromMarket = player.getChipFromMarket();
          Sides whereCanPut = chipFromMarket.putOn(table, false);

          System.out.println("Из рынка вы взяли фишку " + chipFromMarket + ".");

          if (!whereCanPut.equals(Sides.NONE)) {
            System.out.println("Вы положили фишку " + chipFromMarket + ".");
            chipFromMarket.putOn(table, true);
            player.addChipOnTable(chipFromMarket, whereCanPut);
          } else {
            System.out.println("Данную фишку нельзя положить на стол.");
          }
          break;
        case PASS:
          break;
      }
      System.out.print("\nНажмите Enter, чтобы перейти к следующему ходу.");
      INPUT.nextLine();
      System.out.println();

      if (++i > countPlayers - 1) {
        i = 0;
      }
    } while (!isEnd() && !isPlayerEmpty(player));


    System.out.println("\n\n* * * ИГРА ОКОНЧЕНА * * *\nСТОЛ:\n" + table);
    for (i = 0; i < countPlayers; ++i) {
      int n = table.getPlayers()[i].getCountChips();
      if (n > 0) {
        System.out.println("\n" + table.getPlayers()[i].getName()
                + " остался с " + n
                + " фишками(ой): \n" + table.getPlayers()[i]);
      }

      if (!isPlayerEmpty(player) && isEnd()) {
        scores[i] = calculateScore(table.getPlayers()[i]);
        System.out.println("Очки: " + scores[i]);
      }
    }

    if (isPlayerEmpty(player)) {
      System.out.println("\n" + player.getName() + " победил.\n\nПОЗДРАВЛЯЕМ, ВЫ ПОБЕДИЛИ!!");
    } else {
      System.out.println("\n");
      List<Player> winners = getWinners();

      if (winners.size() == 1) {
        System.out.print(winners.get(0).getName() + " набрал наименьшее количество очков.\n\nПОЗДРАВЛЯЕМ, ВЫ ПОБЕДИЛИ!!");
      } else {
        for (int k = 0; k < winners.size(); ++k) {
          System.out.print(winners.get(k).getName());
          if (k == winners.size() - 2) {
            System.out.println(" и ");
          } else {
            if (k < winners.size() - 2) {
              System.out.println(", ");
            }
          }
        }
        System.out.println(" набрали наименьшее количество очков.\n\nПОЗДРАВЛЯЕМ ВСЕХ!!");
      }
    }

    System.out.println("\n\nСпасибо за игру!\n");
  }

  private Moves chooseMove(boolean canPut) {
    Moves move;

    System.out.print("Можете ");

    if (canPut) {
      System.out.print("поставить фишку.");
      move = Moves.PUT;
    } else {
      if (table.getMarket().getCountChips() == 0) {
        System.out.print("пропустить ход.");
        move = Moves.PASS;
      } else {
        System.out.print("взять фишку из рынка.");
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
          System.out.print("\nКакую из фишек, которой вы можете играть, вы хотите поставить? [1 - " + chips.size() + "]: ");
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

    System.out.println("- * - Добро пожаловать в консольную игру Домино - * -");

    do {
      try {
        System.out.println("\nВыберите, что вы хотите сделать:\n[1] Играть\n[2] Выйти");
        op = Integer.parseInt(INPUT.nextLine());
        if (op != 1 && op != 2) {
          System.out.println("Выбранный вариант должен быть равен 1 или 2.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Действительный номер не введён.");
      }
    } while (op < 1 || op > 2);

    if (op == 1) {
      introducePlayers();
    }

    System.out.println("\nДо свидания!");
  }
}
