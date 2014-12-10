package peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import measure.peer.Measure;

public class NapsterPeerFactory implements PeerFactory {

  private static Random rand = new Random(122413);
  private double bestFraction = 0.2;
  private double worstFraction = 0.5;

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    List<Peer> peers = new ArrayList<>();
    int nBest = (int)(nPeers * bestFraction);
    int nWorst = (int)(nPeers * worstFraction);
    
    for (int i = 0; i < nBest; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, (int)(T * 0.85)), slots, privateMeasure, acceptanceMeasure));
    }
    for (int i = nBest; i < nBest + nWorst; ++i) {
      peers.add(new Peer(i, T, getSingleAv(T), slots, privateMeasure, acceptanceMeasure));
    }
    for (int i = nBest + nWorst; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, T/10), slots, privateMeasure, acceptanceMeasure));
    }
    return peers;
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
    return "Napster";
  }

}
