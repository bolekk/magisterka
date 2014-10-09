package simulations;

public interface Measure {

  /**
   * how well this replica fits the requester (from the systems' perspective)
   */
  long score(Peer replica, Peer requester);
}
