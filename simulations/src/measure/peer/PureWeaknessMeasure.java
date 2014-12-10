package measure.peer;

import peer.Peer;

public class PureWeaknessMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    return getAdditional(replica, requester);
  }

  @Override
  public String getName() {
    return "Weakness";
  }
  
}
