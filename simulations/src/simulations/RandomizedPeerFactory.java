package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedPeerFactory implements PeerFactory {

  private static Random rand = new Random(122413);
  private int expectedCoverage;
  
  public RandomizedPeerFactory(int expectedCoverage) {
    this.expectedCoverage = expectedCoverage;
  }

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, expectedCoverage), slots, privateMeasure, acceptanceMeasure));
    }
    return peers;
  }

  private static List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < len; ++i) {
      int nextInt = rand.nextInt(len);
      ret.add(nextInt < expected);
    }
    return ret;
  }

  @Override
  public String getName() {
    return "Uniform";
  }

}
