package simulations;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    final int nPeers = 500;
    final int timeSlots = 24;
    final int replicationSlots = 3;
    final int maxIters = 50;
    
    final int expectedCoverage = 6;
    final PeerFactory peerFactory = new RandomizedPeerFactory(expectedCoverage);
    
    List<Peer> peers = peerFactory.generatePeers(nPeers, timeSlots, replicationSlots, new SumMeasure());
    //Sim sim = new RandomSim(peers);
    Sim sim = new GameSim(peers);

    int effectiveIters = sim.run(maxIters, true);
    System.out.println("EFFECTIVE ITERS = " + effectiveIters);

    List<Stat> results = sim.getResults();
    int globalStrength = 0;
    int sumOrig = 0;
    int usedSlots = 0;
    System.out.print("AVAILABILITY (ORIGINAL AV, REPLICAS I HAVE, MY SLOTS USED)\n");
    for (Stat s : results) {
      globalStrength += s.av - s.peer.getOriginalTotalAv();
      sumOrig += s.peer.getOriginalTotalAv();
      usedSlots += s.peer.getReplicas().size();
      System.out.print(s.av + "(" + s.peer.getOriginalTotalAv() + "; " +
          s.peer.getReplicas().size() + "; " + s.peer.getReplicatedBy().size() + ") ");
    }
    System.out.println("\n");
    System.out.println("NUMBER OF PEERS: " + nPeers);
    System.out.println("TOTAL NUMBER OF SLOTS: " + nPeers * replicationSlots);
    System.out.println("TOTAL USED SLOTS: " + usedSlots);
    System.out.println("ORIG: " + sumOrig);
    System.out.println("SUM OF GAINS: " + globalStrength);
    System.out.println("?OPTIMAL: " + nPeers * timeSlots);
    System.out.println("PERCENT OF GROWTH: " + (double)(globalStrength) / (nPeers * timeSlots - sumOrig));
    System.out.println("PERCENT OF COVER: " + (double)(globalStrength + sumOrig) / (nPeers * timeSlots));
  }
}