package simulations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sim {

  private int iterCount;
  private List<Peer> peers;

  public Sim(List<Peer> peers) {
    this.peers = peers;
    this.iterCount = 0;
  }

  public int run(int iters, boolean stopOnNoChanges) {
    for (int i = 0; i < iters; ++i) {
      iterCount++;
      boolean changed = false;
      for (Peer p : peers) {
        changed |= tryFindBetterReplica(p);
      }
      if (stopOnNoChanges && !changed) {
        return iterCount;
      }
    }
    return iterCount;
  }

  private class Elem implements Comparable<Elem> {
    public Long val;
    public Peer p;
    
    public Elem(long val, Peer p) {
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

  // sort and query according to peer.score()
  private boolean tryFindBetterReplica(Peer peer) {
    boolean changed = false;
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
        if (mine == peer.getSlots()) {
          break; // no need to ask worse peers than I already have if I have no space
        } else {
          continue;
        }
      }
      if (candidate.p.request(peer)) {  // if candidate accepts our replication request
        //System.out.println("ACC " + candidate.toString() + " " + peer.toString());
        changed = true;
        peer.acceptedBy(candidate.p);
        candidate.p.replicate(peer);
        validateSymmetry();
        break;
      }
    }
    return changed;
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
