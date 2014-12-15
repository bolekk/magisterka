package peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import measure.peer.Measure;

import com.google.common.base.Preconditions;

public class CorpParetoPeerFactory implements PeerFactory {

  private final Random random;
  private int[] perm = { 0, 15, 7, 12, 1, 16, 8, 13, 2, 17, 9, 14, 3, 18, 10,
      4, 19, 11, 5, 20, 6, 21, 22, 23 };
  private double paretoShape;

  public CorpParetoPeerFactory(double paretoShape, long randomSeed) {
    this.paretoShape = paretoShape;
    random = new Random(randomSeed);
  }

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots,
      Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();

    addRandomPeers(peers, (int) (nPeers * 0.1), T, 0.316, slots,
        privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int) (nPeers * 0.25), T, 0.29, slots,
        privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int) (nPeers * 0.3), T, 0.25, slots, privateMeasure,
        acceptanceMeasure);
    int rest = nPeers - (int) (nPeers * 0.1) - (int) (nPeers * 0.25)
        - (int) (nPeers * 0.3);
    addRandomPeers(peers, rest, T, 0.11, slots, privateMeasure,
        acceptanceMeasure);

    return peers;
  }

  private void addRandomPeers(List<Peer> peers, int n, int T, double prob,
      int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    int id = peers.size();
    for (int i = 0; i < n; ++i) {
      int lenAv = (int) (T * prob);
      Preconditions.checkArgument(lenAv >= 1 && lenAv <= 8);
      peers.add(new Peer(id++, T, getRandomContAv(T, lenAv), slots,
          privateMeasure, acceptanceMeasure));
    }
  }

  private List<Boolean> getRandomContAv(int T, int len) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < T; i++) {
      ret.add(false);
    }
    int first = selectFirst(T);
    for (int i = 0; i < len; ++i) {
      ret.set((first + i) % T, true);
    }
    return ret;
  }

  private double getRandomPareto(double alpha) {
    double v = random.nextDouble();
    return 1.0 / Math.pow(1.0 - v, 1.0 / alpha);

  }

  // [0, T-1]
  private int selectFirst(int T) {
    return perm[(int) (Math.round(getRandomPareto(paretoShape)) % T)];
  }

  @Override
  public String getName() {
    return "Corporate Pareto";
  }

  /**
   * test
   */
  public static void main(String[] args) {
    final int T = 24;
    final double shape = 1.0;
    CorpParetoPeerFactory fac = new CorpParetoPeerFactory(shape,
        System.currentTimeMillis());
    int[] res = new int[T];
    for (int i = 0; i < 100000; i++) {
      int x = fac.selectFirst(T);
      if (x < T)
        res[x]++;
    }
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < T; i++) {
      b.append(res[i]);
      b.append(" ");
    }
    System.out.println(b.toString());
  }
}
