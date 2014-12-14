package measure.sys;

import java.util.List;

import peer.Peer;
import simulation.Simulation;
import simulation.Stat;

public class BasicSystemMeasure extends SystemMeasure {

  boolean debug;

  public BasicSystemMeasure(boolean debug) {
    this.debug = debug;
  }

  @Override
  public double evaluate(Simulation simulation) {
    List<Stat> results = simulation.getResults();
    int nPeers = results.size();
    Peer samplePeer = results.get(0).peer;
    int timeSlots = samplePeer.getT();
    int replicationSlots = samplePeer.getSlotsNum();

    int globalStrength = 0;
    int sumOrig = 0;
    int usedSlots = 0;
    if (debug)
      System.out
          .print("AVAILABILITY (ORIGINAL AV, REPLICAS I HAVE, MY SLOTS USED)\n");
    for (Stat s : results) {
      globalStrength += s.av - s.peer.getOriginalTotalAv();
      sumOrig += s.peer.getOriginalTotalAv();
      usedSlots += s.peer.getReplicas().size();
      if (debug) {
        System.out.print(s.av + "(" + s.peer.getOriginalTotalAv() + "; "
            + s.peer.getReplicas().size() + "; "
            + s.peer.getReplicatedBy().size() + ") ");
      }
    }

    double result = (double) (globalStrength + sumOrig);
    if (debug) {
      System.out.println("\n");
      System.out.println("   NUMBER OF PEERS: " + nPeers);
      System.out.println("   TOTAL NUMBER OF SLOTS: " + nPeers
          * replicationSlots);
      System.out.println("   TOTAL USED SLOTS: " + usedSlots);
      System.out.println("   ORIG: " + sumOrig);
      System.out.println("   SUM OF GAINS: " + globalStrength);
      System.out.println("   OPTIMAL: " + nPeers * timeSlots);
      System.out.println("   PERCENT OF GROWTH: " + (double) (globalStrength)
          / (nPeers * timeSlots - sumOrig));
      System.out.println("   PERCENT OF COVER: " + result
          / (nPeers * timeSlots));
    }
    return result;
  }

  @Override
  public double evaluateNormalized(Simulation simulation) {
    return evaluate(simulation) / getMaxScore(simulation);
  }

  private double getMaxScore(Simulation simulation) {
    List<Stat> results = simulation.getResults();
    int nPeers = results.size();
    Peer samplePeer = results.get(0).peer;
    int timeSlots = samplePeer.getT();
    return nPeers * timeSlots;
  }

  @Override
  public String getName() {
    return "Basic";
  }

}
