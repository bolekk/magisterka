package peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import measure.peer.Measure;

public class CorpProbPeerFactory implements PeerFactory {

  private final Random random;

  public CorpProbPeerFactory(long randomSeed) {
    random = new Random(randomSeed);
  }
  
  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();
    
    addRandomPeers(peers, (int)(nPeers * 0.1), T, 0.95, slots, privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int)(nPeers * 0.25), T, 0.87, slots, privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int)(nPeers * 0.3), T, 0.75, slots, privateMeasure, acceptanceMeasure);
    int rest = nPeers - (int)(nPeers * 0.1) - (int)(nPeers * 0.25) - (int)(nPeers * 0.3);
    addRandomPeers(peers, rest, T, 0.33, slots, privateMeasure, acceptanceMeasure);

    return peers;
  }

  private void addRandomPeers(List<Peer> peers, int n, int T, double prob, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    int id = peers.size();
    for (int i = 0; i < n; ++i) {
      peers.add(new Peer(id++, T, getRandomAv(T, (int)(T * prob)), slots, privateMeasure, acceptanceMeasure));
    }
  }

  private List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    boolean anything = false;
    for (int i = 0; i < len; ++i) {
      int nextInt = random.nextInt(len);
      ret.add(nextInt < expected);
      if (nextInt < expected) {
        anything = true;
      }
    }
    if (anything) {
      return ret;
    } else {
      return getSingleAv(len);
    }
  }

  private List<Boolean> getSingleAv(int len) {
    List<Boolean> ret = new ArrayList<>();
    int available = random.nextInt(len);
    for (int i = 0; i < len; ++i) {
      ret.add(i == available);
    }
    return ret;
  }

  @Override
  public String getName() {
    return "Corporate probabilistic";
  }

}
