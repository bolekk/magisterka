package simulations;

public class CoverMeasure extends Measure {

  // how good is peer b for peer a
	@Override
	public int measure(Peer a, Peer b) {
    int adds = 0;
    int copies = 0;
    for (int i = 0; i < b.getT(); ++i) {
      if (b.getAv().get(i)) {
        if (a.sumAv.get(i)) ++copies;
        else ++adds;
      }
    }
    //return adds * SHIFT + (SHIFT - 1 - copies);
    return a.nAv + adds;
	}

}
