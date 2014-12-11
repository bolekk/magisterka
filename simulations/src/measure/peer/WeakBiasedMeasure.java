package measure.peer;

import peer.Peer;

public class WeakBiasedMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    return getStrength(requester) + 2 * getAdditional(replica, requester);
  }

  @Override
  public String getName() {
    return "Half-weak";
  }
  
}
