package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peer.Peer;

public class RandomizedSimulation extends Simulation {

  private final Random random;

  public RandomizedSimulation(List<Peer> peers, long randomSeed) {
    super(peers);
    random = new Random(randomSeed);
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
