package simulation;

import java.util.ArrayList;
import java.util.List;

import peer.Peer;

public abstract class Simulation {

  private int iterCount;
  protected List<Peer> peers;

  public Simulation(List<Peer> peers) {
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

  protected void validateSymmetry() {
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

  protected abstract boolean tryFindBetterReplica(Peer peer);
}
