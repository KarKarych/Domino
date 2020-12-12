package ru.vsu.dominoes.p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Peer {
  private final Socket socket;
  private DataInputStream dataInputStream;
  private DataOutputStream dataOutputStream;

  public Peer(Socket socket) {
    this.socket = socket;

    try {
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public String get() {
    try {
      return dataInputStream.readUTF();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void send(String text) {
    try {
      dataOutputStream.writeUTF(text);
      dataOutputStream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void closeSocket() {
    try {
      dataInputStream.close();
      dataOutputStream.close();
      socket.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
