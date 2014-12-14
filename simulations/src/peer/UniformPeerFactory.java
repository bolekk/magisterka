package peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import measure.peer.Measure;

public class UniformPeerFactory implements PeerFactory {

  private final Random random;
  private int expectedCoverage;

  public UniformPeerFactory(int expectedCoverage, long randomSeed) {
    this.expectedCoverage = expectedCoverage;
    this.random = new Random(randomSeed);
  }

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots,
      Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, expectedCoverage), slots,
          privateMeasure, acceptanceMeasure));
    }
    return peers;
  }

  private List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < len; ++i) {
      int nextInt = random.nextInt(len);
      ret.add(nextInt < expected);
    }
    return ret;
  }

  @Override
  public String getName() {
    return "Uniform";
  }
}
