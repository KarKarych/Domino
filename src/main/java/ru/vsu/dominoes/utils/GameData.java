package ru.vsu.dominoes.utils;

import ru.vsu.dominoes.model.enums.PlayerType;
import ru.vsu.dominoes.model.players.Player;
import ru.vsu.dominoes.p2p.Peer;

public class GameData {
  private final PlayerType playerType;
  private final Peer peer;
  private String[] names;
  private Player[] players;

  public GameData(PlayerType playerType, Peer peer, String[] names) {
    this.playerType = playerType;
    this.peer = peer;
    this.names = names;
  }

  public GameData(PlayerType playerType, Peer peer, Player[] players) {
    this.playerType = playerType;
    this.peer = peer;
    this.players = players;
  }

  public Player[] getPlayers() {
    return players;
  }

  public void setPlayers(Player[] players) {
    this.players = players;
  }

  public PlayerType getPlayerType() {
    return playerType;
  }

  public Peer getPeer() {
    return peer;
  }

  public String[] getNames() {
    return names;
  }
}
