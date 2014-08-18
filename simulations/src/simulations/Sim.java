package simulations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sim {

  private final int DEF_SLOTS = 3;
  private final int T;
  private int nPeers;
  private List<Peer> peers;
  private Random rand;
  private Measure measure;
  private int slots = DEF_SLOTS;

  public Sim(int nPeers, int T, Measure measure, int slots) {
    this.nPeers = nPeers;
    this.T = T;
    this.rand = new Random(122413);
    this.measure = measure;
    this.slots = slots;
    init();
  }

  private void init() {
    peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(T, randomAv(T, 8), slots));
    }
  }

  public void run(int iters) {
    for (int i = 0; i < iters; ++i) {
      for (Peer p : peers) {
        find(p);
      }
    }
  }
  
  public List<Stat> getResults() {
    List<Stat> stats = new ArrayList<>();
    for (Peer peer : peers) {
      stats.add(new Stat(peer, peer.getSumAv()));
    }
    return stats;
  }
  
  private class Elem implements Comparable<Elem> {
    public Integer val;
    public Peer peer;
    
    public Elem(int val, Peer p) {
      this.val = val;
      this.peer = p;
    }

    @Override
    public int compareTo(Elem o) {
      return -val.compareTo(o.val);
    }
  }
  
  // try to find a single better replica
  private void find(Peer peer) {
    List<Elem> candidates = new ArrayList<>();
    for (Peer p : peers) {
      if (p == peer) continue;
      candidates.add(new Elem(measure.measure(peer, p), p));
    }
    Collections.sort(candidates);
    for (Elem e : candidates) {
      if (e.peer.offer(peer, e.val)) {
        peer.acceptedBy(e.peer);
        e.peer.replicate(peer);
        break;
      }
    }
  }

  private List<Boolean> randomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < len; ++i) {
      int nextInt = rand.nextInt(T);
      ret.add(nextInt < expected);
    }
    return ret;
  }
}
