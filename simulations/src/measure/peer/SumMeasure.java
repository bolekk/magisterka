package measure.peer;

import peer.Peer;

/**
 * Requester's own strength + how many new slots the replica can add to
 * requester's ORIGINAL coverage.
 */
public class SumMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    return getStrength(requester) + getAdditional(replica, requester);
  }

  @Override
  public String getName() {
    return "Sum";
  }

}
