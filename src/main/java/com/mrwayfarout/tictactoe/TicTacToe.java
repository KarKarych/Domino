package com.mrwayfarout.tictactoe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToe implements Runnable {
  private final String ip;
  private int port;
  private final int WIDTH = 506;
  private final int HEIGHT = 527;

  private final Painter painter;
  private DataOutputStream dos;
  private DataInputStream dis;

  private ServerSocket serverSocket;

  private BufferedImage board;
  private BufferedImage redX;
  private BufferedImage blueX;
  private BufferedImage redCircle;
  private BufferedImage blueCircle;

  private final String[] spaces = new String[9];

  private boolean yourTurn = false;
  private boolean circle = true;
  private boolean accepted = false;
  private boolean unableToCommunicateWithOpponent = false;
  private boolean won = false;
  private boolean enemyWon = false;
  private boolean tie = false;

  private final int lengthOfSpace = 160;
  private int errors = 0;
  private int firstSpot = -1;
  private int secondSpot = -1;

  private final Font font = new Font("Verdana", Font.BOLD, 32);
  private final Font smallerFont = new Font("Verdana", Font.BOLD, 20);
  private final Font largerFont = new Font("Verdana", Font.BOLD, 50);

  private final int[][] wins = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

  /**
   * <pre>
   * 0, 1, 2
   * 3, 4, 5
   * 6, 7, 8
   * </pre>
   */

  public TicTacToe() {
    System.out.println("Please input the IP: ");
    Scanner scanner = new Scanner(System.in);
    ip = scanner.nextLine();
    System.out.println("Please input the port: ");
    port = scanner.nextInt();
    while (port < 1 || port > 65535) {
      System.out.println("The port you entered was invalid, please input another port: ");
      port = scanner.nextInt();
    }

    loadImages();

    painter = new Painter();
    painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

    if (!connect()) initializeServer();

    JFrame frame = new JFrame();
    frame.setTitle("Tic-Tac-Toe");
    frame.setContentPane(painter);
    frame.setSize(WIDTH, HEIGHT);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);

    Thread thread = new Thread(this, "TicTacToe");
    thread.start();
  }

  public void run() {
    try {
      while (true) {
        tick();
        painter.repaint();

        if (!circle && !accepted) {
          listenForServerRequest();
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void render(Graphics g) {
    g.drawImage(board, 0, 0, null);
    if (unableToCommunicateWithOpponent) {
      String unableToCommunicateWithOpponentString = "Unable to communicate with opponent.";
      drawEmptyField(g, smallerFont, unableToCommunicateWithOpponentString);
      return;
    }

    if (accepted) {
      for (int i = 0; i < spaces.length; i++) {
        if (spaces[i] != null) {
          if (spaces[i].equals("X")) {
            drawField(g, i, redX, blueX);
          } else if (spaces[i].equals("O")) {
            drawField(g, i, blueCircle, redCircle);
          }
        }
      }

      if (won || enemyWon) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(10));
        g.setColor(Color.BLACK);
        g.drawLine(firstSpot % 3 * lengthOfSpace + 10 * firstSpot % 3 + lengthOfSpace / 2,
                (firstSpot / 3) * lengthOfSpace + 10 * (firstSpot / 3) + lengthOfSpace / 2,
                secondSpot % 3 * lengthOfSpace + 10 * secondSpot % 3 + lengthOfSpace / 2,
                (secondSpot / 3) * lengthOfSpace + 10 * (secondSpot / 3) + lengthOfSpace / 2);

        g.setColor(Color.RED);
        g.setFont(largerFont);
        if (won) {
          String wonString = "You won!";
          int stringWidth = g2.getFontMetrics().stringWidth(wonString);
          g.drawString(wonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
        } else if (enemyWon) {
          String enemyWonString = "Opponent won!";
          int stringWidth = g2.getFontMetrics().stringWidth(enemyWonString);
          g.drawString(enemyWonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
        }
      }

      if (tie) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.BLACK);
        g.setFont(largerFont);
        String tieString = "Game ended in a tie.";
        int stringWidth = g2.getFontMetrics().stringWidth(tieString);
        g.drawString(tieString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
      }
    } else {
      String waitingString = "Waiting for another player";
      drawEmptyField(g, font, waitingString);
    }

  }

  private void drawEmptyField(Graphics g, Font font, String waitingString) {
    g.setColor(Color.RED);
    g.setFont(font);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
    g.drawString(waitingString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
  }

  private void drawField(Graphics g, int i, BufferedImage redX, BufferedImage blueX) {
    int x = (i % 3) * lengthOfSpace + 10 * (i % 3);
    int y = (i / 3) * lengthOfSpace + 10 * (i / 3);

    if (circle) {
      g.drawImage(redX, x, y, null);
    } else {
      g.drawImage(blueX, x, y, null);
    }
  }

  private void tick() {
    if (errors >= 10) unableToCommunicateWithOpponent = true;

    if (!yourTurn && !unableToCommunicateWithOpponent) {
      try {
        int space = dis.readInt();
        if (circle) spaces[space] = "X";
        else spaces[space] = "O";
        checkForEnemyWin();
        checkForTie();
        yourTurn = true;
      } catch (IOException e) {
        e.printStackTrace();
        errors++;
      }
    }
  }

  private void checkForWin() {
    for (int[] win : wins) {
      if (circle) {
        if (spaces[win[0]].equals("O") && spaces[win[1]].equals("O") && spaces[win[2]].equals("O")) {
          firstSpot = win[0];
          secondSpot = win[2];
          won = true;
        }
      } else {
        if (spaces[win[0]].equals("X") && spaces[win[1]].equals("X") && spaces[win[2]].equals("X")) {
          firstSpot = win[0];
          secondSpot = win[2];
          won = true;
        }
      }
    }
  }

  private void checkForEnemyWin() {
    for (int[] win : wins) {
      if (circle) {
        if (spaces[win[0]].equals("X") && spaces[win[1]].equals("X") && spaces[win[2]].equals("X")) {
          firstSpot = win[0];
          secondSpot = win[2];
          enemyWon = true;
        }
      } else {
        if (spaces[win[0]].equals("O") && spaces[win[1]].equals("O") && spaces[win[2]].equals("O")) {
          firstSpot = win[0];
          secondSpot = win[2];
          enemyWon = true;
        }
      }
    }
  }

  private void checkForTie() {
    for (String space : spaces) {
      if (space == null) {
        return;
      }
    }
    tie = true;
  }

  private void listenForServerRequest() {
    Socket socket;
    try {
      socket = serverSocket.accept();
      dos = new DataOutputStream(socket.getOutputStream());
      dis = new DataInputStream(socket.getInputStream());
      accepted = true;
      System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean connect() {
    try {
      Socket socket = new Socket(ip, port);
      dos = new DataOutputStream(socket.getOutputStream());
      dis = new DataInputStream(socket.getInputStream());
      accepted = true;
    } catch (IOException e) {
      System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
      return false;
    }
    System.out.println("Successfully connected to the server.");
    return true;
  }

  private void initializeServer() {
    try {
      serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
    } catch (Exception e) {
      e.printStackTrace();
    }
    yourTurn = true;
    circle = false;
  }

  private void loadImages() {
    try {
      board = ImageIO.read(new File("res/board.png"));
      redX = ImageIO.read(new File("res/redX.png"));
      redCircle = ImageIO.read(new File("res/redCircle.png"));
      blueX = ImageIO.read(new File("res/blueX.png"));
      blueCircle = ImageIO.read(new File("res/blueCircle.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new TicTacToe();
  }

  private class Painter extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    public Painter() {
      setFocusable(true);
      requestFocus();
      setBackground(Color.WHITE);
      addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      render(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (accepted) {
        if (yourTurn && !unableToCommunicateWithOpponent && !won && !enemyWon) {
          int x = e.getX() / lengthOfSpace;
          int y = e.getY() / lengthOfSpace;
          y *= 3;
          int position = x + y;

          if (spaces[position] == null) {
            if (!circle) spaces[position] = "X";
            else spaces[position] = "O";
            yourTurn = false;
            repaint();
            Toolkit.getDefaultToolkit().sync();

            try {
              dos.writeInt(position);
              dos.flush();
            } catch (IOException e1) {
              errors++;
              e1.printStackTrace();
            }

            System.out.println("DATA WAS SENT");
            checkForWin();
            checkForTie();

          }
        }
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

  }
}
