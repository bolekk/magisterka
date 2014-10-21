package simulations;

import java.util.List;

public interface PeerFactory {
  List<Peer> generatePeers(int nPeers, int T, int slots, Measure measure);
}
