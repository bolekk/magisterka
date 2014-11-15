package simulations;

/**
 * Requester's own strength + how many new slots the replica can add
 * to requester's CURRENT coverage, assuming it might need to remove some other replica.
 */
public class RealSumMeasure extends Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    long strength = requester.getOriginalTotalAv();
    long additional = requester.realScore(replica);
    return strength + additional;
  }

  @Override
  public String getName() {
    return "Real Sum";
  }

}
