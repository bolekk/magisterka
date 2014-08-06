package simulations;

import java.util.ArrayList;
import java.util.List;

public class Peer {
  private static final int SHIFT = 1000;
  
  private final int T;
  private List<Boolean> av;
  private List<Boolean> sumAv;
  private int slots;
  private int slotsAvail;
  private List<Peer> replicas;

  public Peer(int T, List<Boolean> av, int slots) {
    this.T = T;
    this.av = av;
    this.sumAv = new ArrayList<>(av);
    this.slots = slots;
    this.slotsAvail = slots;
    this.replicas = new ArrayList<>();
  }

  // how good this peer is for us
  public int score(Peer p) {
    int adds = 0;
    int copies = 0;
    for (int i = 0; i < T; ++i) {
      if (p.av.get(i)) {
        if (sumAv.get(i)) ++copies;
        else ++adds;
      }
    }
    return adds * SHIFT + (SHIFT - 1 - copies); 
  }
  
  public int getSumAv() {
    int sum = 0;
    for (Boolean b : sumAv) {
      if (b) ++sum;
    }
    return sum;
  }

  public void acceptedBy(Peer peer) {
    replicas.add(peer);
    for (int i = 0; i < T; ++i) {
      sumAv.set(i, sumAv.get(i) || peer.av.get(i));
    }
  }

  public boolean offer(Peer peer) {
    return slotsAvail > 0;
  }

  public void replicate(Peer peer) {
    --slotsAvail;
  }
}
