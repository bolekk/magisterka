package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedContPeerFactory implements PeerFactory {

  private static Random rand = new Random(722413);
  private int expectedCoverage;
  
  public RandomizedContPeerFactory(int expectedCoverage) {
    this.expectedCoverage = expectedCoverage;
  }

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomContAv(T, expectedCoverage), slots, privateMeasure, acceptanceMeasure));
    }
    return peers;
  }

  private List<Boolean> getRandomContAv(int T, int len) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < T; i++) {
      ret.add(false);
    }
    int first = rand.nextInt(T);
    for (int i = 0; i < len; ++i) {
      ret.set((first + i) % T, true);
    }
    return ret;
  }


  @Override
  public String getName() {
    return "Uniform continuous";
  }

}
