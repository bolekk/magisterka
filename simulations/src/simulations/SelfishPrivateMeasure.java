package simulations;

/**
 * Requester's own strength + how many new slots the replica can add
 * to requester's CURRENT coverage, assuming it might need to remove some other replica.
 */
public class SelfishPrivateMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    //long strength = requester.getOriginalTotalAv();
    long additional = requester.realScore(replica);
    return additional;
  }

  @Override
  public String getName() {
    return "SelfishPrivateMeasure";
  }

}
