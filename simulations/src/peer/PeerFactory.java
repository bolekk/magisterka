package peer;

import java.util.List;

import measure.peer.Measure;

public interface PeerFactory {
  List<Peer> generatePeers(int nPeers, int T, int slots,
      Measure privateMeasure, Measure acceptanceMeasure);

  String getName();
}
