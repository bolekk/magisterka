package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KrzPeerFactory implements PeerFactory {

  private static Random rand = new Random(99413);

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure measure) {
    List<Peer> peers = new ArrayList<>();
    
    addRandomPeers(peers, (int)(nPeers * 0.1), T, 0.95, slots, measure);
    addRandomPeers(peers, (int)(nPeers * 0.25), T, 0.87, slots, measure);
    addRandomPeers(peers, (int)(nPeers * 0.3), T, 0.75, slots, measure);
    int rest = nPeers - (int)(nPeers * 0.1) - (int)(nPeers * 0.25) - (int)(nPeers * 0.3);
    addRandomPeers(peers, rest, T, 0.33, slots, measure);

    return peers;
  }

  private void addRandomPeers(List<Peer> peers, int n, int T, double prob, int slots, Measure measure) {
    int id = peers.size();
    for (int i = 0; i < n; ++i) {
      peers.add(new Peer(id++, T, getRandomAv(T, (int)(T * prob)), slots, measure));
    }
  }

  private List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    boolean anything = false;
    for (int i = 0; i < len; ++i) {
      int nextInt = rand.nextInt(len);
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
    int available = rand.nextInt(len);
    for (int i = 0; i < len; ++i) {
      ret.add(i == available);
    }
    return ret;
  }

  @Override
  public String getName() {
    return "KRZ";
  }

}
