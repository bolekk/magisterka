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
  private int replicationSlots = DEF_SLOTS;

  public Sim(int nPeers, int T, Measure measure, int replicationSlots) {
    this.nPeers = nPeers;
    this.T = T;
    this.rand = new Random(122413);
    this.measure = measure;
    this.replicationSlots = replicationSlots;
    init();
  }

  private void init() {
    peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, 8), replicationSlots, measure));
    }
  }

  private List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < len; ++i) {
      int nextInt = rand.nextInt(T);
      ret.add(nextInt < expected);
    }
    return ret;
  }

  public void run(int iters) {
    for (int i = 0; i < iters; ++i) {
      for (Peer p : peers) {
        tryFindBetterReplica(p);
      }
    }
  }

  private class Elem implements Comparable<Elem> {
    public Integer val;
    public Peer p;
    
    public Elem(int val, Peer p) {
      this.val = val;
      this.p = p;
    }

    @Override
    public int compareTo(Elem o) {
      return -val.compareTo(o.val);
    }
    
    @Override
    public String toString() {
      return "Elem[" + p.toString() + ", " + val + "]";
    }
  }

  private void tryFindBetterReplica(Peer peer) {
    List<Elem> candidates = new ArrayList<>();
    for (Peer other : peers) {
      if (other == peer) continue;
      candidates.add(new Elem(peer.score(other), other));
    }
    Collections.sort(candidates);
    int mine = 0;
    for (Elem candidate : candidates) {
      if (peer.getReplicas().contains(candidate.p)) {
        mine++;
        if (mine == peer.getReplicas().size()) {
          break; // no need to ask worse peers than I already have
        } else {
          continue;
        }
      }
      if (candidate.p.request(peer)) {  // if candidate accepts our replication request
        peer.acceptedBy(candidate.p);
        candidate.p.replicate(peer);
        validateSymmetry();
        break;
      }
    }
  }

  private void validateSymmetry() {
    boolean correct = true;
    String msg = "";
    for (Peer peer : peers) {
      for (Peer replica : peer.getReplicas()) {
        if (!replica.getReplicatedBy().contains(peer)) {
          correct = false;
          msg = peer + "," + replica;
          break;
        }
      }
      for (Peer replicatedBy : peer.getReplicatedBy()) {
        if (!replicatedBy.getReplicas().contains(peer)) {
          correct = false;
          msg = peer + "," + replicatedBy;
          break;
        }
      }
    }
    if (!correct) {
      throw new RuntimeException("Incorrect " + msg);
    }
  }

  public List<Stat> getResults() {
    List<Stat> stats = new ArrayList<>();
    for (Peer p : peers) {
      stats.add(new Stat(p, p.getTotalAv()));
    }
    return stats;
  }
}
