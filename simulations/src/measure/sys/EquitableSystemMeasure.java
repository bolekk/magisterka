package measure.sys;

import java.util.List;

import peer.Peer;
import simulation.Simulation;
import simulation.Stat;

public class EquitableSystemMeasure extends SystemMeasure {

  boolean debug;

  public EquitableSystemMeasure(boolean debug) {
    this.debug = debug;
  }

  @Override
  public double evaluate(Simulation simulation) {
    List<Stat> results = simulation.getResults();

    double globalStrength = 0.0;
    if (debug) System.out.print("AVAILABILITY (ORIGINAL AV, REPLICAS I HAVE, MY SLOTS USED)\n");
    for (Stat s : results) {
      globalStrength += Math.sqrt(s.av);
      if (debug) {
        System.out.print(s.av + "(" + s.peer.getOriginalTotalAv() + "; " +
            s.peer.getReplicas().size() + "; " + s.peer.getReplicatedBy().size() + ") ");
      }
    }

    return globalStrength;
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
    return (double) nPeers * Math.sqrt(timeSlots);
  }

  @Override
  public String getName() {
    return "Equitable";
  }

}
