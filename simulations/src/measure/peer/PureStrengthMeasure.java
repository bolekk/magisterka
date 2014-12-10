package measure.peer;

import peer.Peer;

public class PureStrengthMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    return getStrength(requester);
  }

  @Override
  public String getName() {
    return "Strength";
  }
  
}
