package peer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import measure.peer.Measure;

import com.google.common.base.Preconditions;

public class Peer {

  private final int T;
  private final int uniqueId;
  private List<Boolean> av;
  private List<Boolean> sumAv; // my av + my replicas
  private int slots;

  private Measure privateMeasure;
  private Measure acceptanceMeasure;
  private Set<Peer> myReplicas;
  private Set<ReplicatedPeer> peersIReplicate;
  private long weakestScore;

  public Peer(int uniqueId, int T, List<Boolean> av, int slots,
      Measure privateMeasure, Measure acceptanceMeasure) {
    this.uniqueId = uniqueId;
    this.privateMeasure = privateMeasure;
    this.acceptanceMeasure = acceptanceMeasure;
    this.T = T;
    this.av = av;
    this.sumAv = new ArrayList<>(av);
    this.slots = slots;
    this.myReplicas = new HashSet<>();
    this.peersIReplicate = new HashSet<>();
  }

  // peers I replicate

  private class ReplicatedPeer {
    public long score;
    public Peer p;

    public ReplicatedPeer(Peer p, long score) {
      this.score = score;
      this.p = p;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof ReplicatedPeer) {
        return ((ReplicatedPeer) other).p.uniqueId == p.uniqueId;
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return p.uniqueId;
    }

    @Override
    public String toString() {
      return p.toString();
    }
  }

  public boolean request(Peer peerToReplicate) {
    if (peersIReplicate.size() < slots) {
      return true;
    } else {
      recalculateScores();
      long peerScore = acceptanceMeasure.score(this, peerToReplicate);
      return peerScore > weakestScore;
    }
  }

  public void replicate(Peer peer) {
    if (peersIReplicate.size() == slots) {
      removeWeakestIReplicate();
    }
    peersIReplicate.add(new ReplicatedPeer(peer, 0));
  }

  private void recalculateScores() {
    weakestScore = -1;
    for (ReplicatedPeer repPeer : peersIReplicate) {
      repPeer.score = acceptanceMeasure.score(this, repPeer.p);
      if (weakestScore == -1)
        weakestScore = repPeer.score;
      else
        weakestScore = Math.min(weakestScore, repPeer.score);
    }
  }

  private void removeWeakestIReplicate() {
    boolean removed = false;
    for (ReplicatedPeer repPeer : peersIReplicate) {
      if (repPeer.score == weakestScore) {
        peersIReplicate.remove(repPeer);
        repPeer.p.removeReplica(this);
        removed = true;
        break;
      }
    }
    Preconditions.checkArgument(removed);
    calculateSumAv();
  }

  // my replicas

  // how good this peer is for us
  public long score(Peer potentialReplica) {
    return privateMeasure.score(potentialReplica, this);
    // return basicScore(potentialReplica);
    // return realScore(potentialReplica);
  }

  // how many points can it add to our current score
  public int basicScore(Peer potentialReplica) {
    int adds = 0;
    for (int i = 0; i < T; ++i) {
      if (potentialReplica.av.get(i)) {
        if (!sumAv.get(i))
          ++adds;
      }
    }
    return adds;
  }

  // how many can it add assuming we need to remove our weakest replica
  public int realScore(Peer potentialReplica) {
    Set<Peer> copy = new HashSet<Peer>(myReplicas);
    copy.add(potentialReplica);
    if (myReplicas.size() < slots) {
      return getTotalAv(calculateSumAv(copy));
    } else { // one needs to be removed
      int maxScore = Integer.MIN_VALUE;
      for (Peer currReplica : myReplicas) {
        copy.remove(currReplica);
        int currScore = getTotalAv(calculateSumAv(copy));
        if (currScore > maxScore) {
          maxScore = currScore;
        }
        copy.add(currReplica);
      }
      return maxScore;
    }
  }

  public void acceptedBy(Peer peer) {
    if (myReplicas.size() == slots) {
      removeWeakestOfMyReplicas(peer);
    }
    myReplicas.add(peer);
    calculateSumAv();
  }

  private void removeReplica(Peer peer) {
    myReplicas.remove(peer);
    calculateSumAv();
  }

  /**
   * Remove the one that gives most profit assuming newReplica is added.
   */
  private void removeWeakestOfMyReplicas(Peer newReplica) {
    Set<Peer> copy = new HashSet<Peer>(myReplicas);
    long maxScore = Long.MIN_VALUE;
    Peer weakestPeer = null;
    copy.add(newReplica);
    for (Peer currReplica : myReplicas) {
      copy.remove(currReplica);
      int currScore = getTotalAv(calculateSumAv(copy));
      if (currScore > maxScore) {
        maxScore = currScore;
        weakestPeer = currReplica;
      }
      copy.add(currReplica);
    }
    if (weakestPeer != null) {
      weakestPeer.peersIReplicate.remove(new ReplicatedPeer(this, 0));
      removeReplica(weakestPeer);
    }
  }

  // misc

  private void calculateSumAv() {
    List<Boolean> peersSum = calculateSumAv(myReplicas);
    for (int i = 0; i < T; ++i) {
      sumAv.set(i, av.get(i) || peersSum.get(i));
    }
  }

  private List<Boolean> calculateSumAv(Set<Peer> peers) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < T; ++i) {
      ret.add(false);
      for (Peer replica : peers) {
        if (replica.getAv().get(i)) {
          ret.set(i, true);
          break;
        }
      }
    }
    return ret;
  }

  public int getOriginalTotalAv() {
    int sum = 0;
    for (Boolean b : av) {
      if (b)
        ++sum;
    }
    return sum;
  }

  public int getTotalAv() {
    return getTotalAv(this.sumAv);
  }

  private int getTotalAv(List<Boolean> sumAv) {
    int sum = 0;
    for (Boolean b : sumAv) {
      if (b)
        ++sum;
    }
    return sum;
  }

  public List<Boolean> getAv() {
    return av;
  }

  public Set<Peer> getReplicas() {
    return myReplicas;
  }

  public Set<Peer> getReplicatedBy() {
    Set<Peer> ret = new HashSet<>();
    for (ReplicatedPeer peer : peersIReplicate) {
      ret.add(peer.p);
    }
    return ret;
  }

  /**
   * @return number of replication slots
   */
  public int getSlotsNum() {
    return slots;
  }

  /**
   * @return number of time slots
   */
  public int getT() {
    return T;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Peer) {
      return ((Peer) other).uniqueId == uniqueId;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return uniqueId;
  }

  @Override
  public String toString() {
    return "Peer[" + uniqueId + "]";
  }
}
