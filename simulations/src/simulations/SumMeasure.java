package simulations;

/**
 * Requester's own strength + how many new slots the replica can add
 * to requester's ORIGINAL coverage.
 */
public class SumMeasure implements Measure {

  @Override
  public long score(Peer replica, Peer requester) {
    long strength = requester.getOriginalTotalAv();
    long additional = 0;
    for (int i = 0; i < replica.getAv().size(); ++i) {
      if (replica.getAv().get(i)) {
        if (!requester.getAv().get(i)) ++additional;
      }
    }
    return strength + additional;
  }

}
