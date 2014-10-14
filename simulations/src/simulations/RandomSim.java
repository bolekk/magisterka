package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSim extends Sim {
  
  Random random = new Random(13244441);

  public RandomSim(List<Peer> peers) {
    super(peers);
  }
  
  // group peers randomly
  protected boolean tryFindBetterReplica(Peer peer) {
    if (peer.getSlotsNum() == peer.getReplicas().size())
      return false;
    List<Peer> candidates = new ArrayList<>();
    for (Peer other : peers) {
      if (other != peer && other.getReplicatedBy().size() < peer.getSlotsNum())
        candidates.add(other);
    }
    if (candidates.isEmpty())
      return false;
    Peer randomPeer = candidates.get(random.nextInt(candidates.size()));
    peer.acceptedBy(randomPeer);
    randomPeer.replicate(peer);
    validateSymmetry();
    return true;
  }
}
