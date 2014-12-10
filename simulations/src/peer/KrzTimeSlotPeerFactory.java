package peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import measure.peer.Measure;

import com.google.common.base.Preconditions;

public class KrzTimeSlotPeerFactory implements PeerFactory {

  private static Random rand = new Random(99113);
  private List<Integer> perm = new ArrayList<>();
  private double paretoShape;

  public KrzTimeSlotPeerFactory(double paretoShape) {
    this.paretoShape = paretoShape;
  }

  @Override
  public List<Peer> generatePeers(int nPeers, int T, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    for (int i = 0; i < T; i++) {
      perm.add(i);
    }
    Collections.shuffle(perm);
    
    List<Peer> peers = new ArrayList<>();
    
    addRandomPeers(peers, (int)(nPeers * 0.1), T, 0.316, slots, privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int)(nPeers * 0.25), T, 0.29, slots, privateMeasure, acceptanceMeasure);
    addRandomPeers(peers, (int)(nPeers * 0.3), T, 0.25, slots, privateMeasure, acceptanceMeasure);
    int rest = nPeers - (int)(nPeers * 0.1) - (int)(nPeers * 0.25) - (int)(nPeers * 0.3);
    addRandomPeers(peers, rest, T, 0.11, slots, privateMeasure, acceptanceMeasure);

    return peers;
  }

  private void addRandomPeers(List<Peer> peers, int n, int T, double prob, int slots, Measure privateMeasure, Measure acceptanceMeasure) {
    int id = peers.size();
    for (int i = 0; i < n; ++i) {
      int lenAv = (int)(T * prob);
      Preconditions.checkArgument(lenAv >= 1 && lenAv <= 8);
      peers.add(new Peer(id++, T, getRandomContAv(T, lenAv), slots, privateMeasure, acceptanceMeasure));
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
    double v = rand.nextDouble(); 
    while (v == 0){ 
        v = rand.nextDouble(); 
    }
    return 1.0 / Math.pow(v, 1.0/alpha); 
  }

  private int selectFirst(int T) {
    int random = (int)getRandomPareto(paretoShape);
    random--;
    if (random >= T) {
      random = T;
    }
    return random;
  }

  @Override
  public String getName() {
    return "KRZ_TIME_SLOT";
  }
}
