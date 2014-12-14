package simulation;

import peer.Peer;

public class Stat {
  public int av;
  public Peer peer;

  public Stat(Peer peer, int av) {
    this.av = av;
    this.peer = peer;
  }
}
