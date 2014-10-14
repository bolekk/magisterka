package simulations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSim extends Sim {

  public GameSim(List<Peer> peers) {
    super(peers);
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
  protected boolean tryFindBetterReplica(Peer peer) {
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
        if (mine == peer.getSlotsNum()) {
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
}
