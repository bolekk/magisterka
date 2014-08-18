package simulations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Peer {
  private static final int SHIFT = 1000;
  
  private final int T;
  private List<Boolean> av;
  int nAv;
  List<Boolean> sumAv;
  private int slots;
  private int slotsAvail;
  private Set<Peer> myReplicas;
  private Set<Peer> iReplicate;

  public Peer(int T, List<Boolean> av, int slots) {
    this.T = T;
    this.setAv(av);
    this.sumAv = new ArrayList<>(av);
    this.nAv = getSumAv();
    this.slots = slots;
    this.slotsAvail = slots;
    this.myReplicas = new HashSet<>();
    this.iReplicate = new HashSet<>();
    recalculateSumAv();
  }
  
  public int getT() {
    return T;
  }
  
  public int getSumAv() {
    int sum = 0;
    for (Boolean b : sumAv) {
      if (b) ++sum;
    }
    return sum;
  }

  public void acceptedBy(Peer peer) {
    myReplicas.add(peer);
    recalculateSumAv();
  }

  private void recalculateSumAv() {
    for (int i = 0; i < T; ++i) {
      sumAv.set(i, av.get(i));
      for (Peer replica : myReplicas) {
        if (replica.getAv().get(i)) {
          sumAv.set(i, true);
          break;
        }
      }  
    }
  }

  public boolean offer(Peer peer, Integer val) {
    return slotsAvail > 0;
  }

  public void replicate(Peer peer) {
    iReplicate.add(peer);
    --slotsAvail;
  }

  public List<Boolean> getAv() {
    return av;
  }

  public void setAv(List<Boolean> av) {
    this.av = av;
  }
}
