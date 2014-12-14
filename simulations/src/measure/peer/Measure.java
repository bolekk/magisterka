package measure.peer;

import peer.Peer;

/**
 * Base class for peer measures.
 */
public abstract class Measure {

  /**
   * how well this replica fits the requester (from the systems' perspective)
   */
  public abstract long score(Peer replica, Peer requester);

  protected long getStrength(Peer peer) {
    return peer.getOriginalTotalAv();
  }

  protected long getAdditional(Peer replica, Peer requester) {
    long additional = 0;
    for (int i = 0; i < replica.getAv().size(); ++i) {
      if (replica.getAv().get(i)) {
        if (!requester.getAv().get(i)) {
          ++additional;
        }
      }
    }
    return additional;
  }

  public abstract String getName();
}
