package measure.peer;

import peer.Peer;

public class HalfStrongMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    return 2 * getStrength(requester) + getAdditional(replica, requester);
  }

  @Override
  public String getName() {
    return "Half-strong";
  }

}
