package simulations;

import java.util.ArrayList;
import java.util.List;

public class Main {

  private static final int nPeers = 1000;
  private static final int timeSlots = 24;
  private static final int replicationSlots = 4;
  private static final int maxIters = 50;



  public static void main(String[] args) {
    List<PeerFactory> factories = new ArrayList<>();    
    final int expectedCoverage = 18;
    //factories.add(new RandomizedPeerFactory(expectedCoverage));
    //factories.add(new NapsterPeerFactory());
    factories.add(new KrzPeerFactory());

    List<Measure> measures = new ArrayList<>();
    measures.add(new PureWeaknessMeasure());
    measures.add(new WeakBiasedMeasure());
    measures.add(new SumMeasure());
    measures.add(new StrengthBiasedMeasure());
    measures.add(new PureStrengthMeasure());

    for (PeerFactory factory : factories) {
      // games
      for (Measure measure : measures) {
        List<Peer> peers = factory.generatePeers(nPeers, timeSlots, replicationSlots, measure);
        Sim sim = new GameSim(peers);
        runAndPrint(factory.getName(), measure.getName(), sim);
      }
      // random (measure irrelevant)
      List<Peer> peers = factory.generatePeers(nPeers, timeSlots, replicationSlots, new SumMeasure()); 
      Sim sim = new RandomSim(peers);
      runAndPrint(factory.getName(), "Random", sim);
    }
  }

  private static void runAndPrint(String factoryName, String measureName, Sim sim) {
    int effectiveIters = sim.run(maxIters, true);
    
    System.out.println("---- FACTORY: " + factoryName + " ---- MEASURE: " + measureName);
    
    printAllResults(sim, effectiveIters, true);
  }

  private static void printAllResults(Sim sim, int effectiveIters, boolean brief) {
    if (!brief) System.out.println("EFFECTIVE ITERS = " + effectiveIters);

    List<Stat> results = sim.getResults();
    int globalStrength = 0;
    int sumOrig = 0;
    int usedSlots = 0;
    if (!brief) System.out.print("AVAILABILITY (ORIGINAL AV, REPLICAS I HAVE, MY SLOTS USED)\n");
    for (Stat s : results) {
      globalStrength += s.av - s.peer.getOriginalTotalAv();
      sumOrig += s.peer.getOriginalTotalAv();
      usedSlots += s.peer.getReplicas().size();
      if (!brief) {
        System.out.print(s.av + "(" + s.peer.getOriginalTotalAv() + "; " +
            s.peer.getReplicas().size() + "; " + s.peer.getReplicatedBy().size() + ") ");
      }
    }
    if (!brief) {
      System.out.println("\n");
      System.out.println("   NUMBER OF PEERS: " + nPeers);
      System.out.println("   TOTAL NUMBER OF SLOTS: " + nPeers * replicationSlots);
      System.out.println("   TOTAL USED SLOTS: " + usedSlots);
      System.out.println("   ORIG: " + sumOrig);
      System.out.println("   SUM OF GAINS: " + globalStrength);
      System.out.println("   ?OPTIMAL: " + nPeers * timeSlots);
      System.out.println("   PERCENT OF GROWTH: " + (double)(globalStrength) / (nPeers * timeSlots - sumOrig));
    }
    System.out.println("   PERCENT OF COVER: " + (double)(globalStrength + sumOrig) / (nPeers * timeSlots));
  }
}