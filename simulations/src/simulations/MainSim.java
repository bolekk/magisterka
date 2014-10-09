package simulations;

import java.util.List;

public class MainSim {

  public static void main(String[] args) {
    final int nPeers = 40;
    final int T = 24;
    final int slots = 2;
    Sim sim = new Sim(nPeers, T, new SumMeasure(), slots);
    sim.run(100);
    List<Stat> results = sim.getResults();
    int globalStrength = 0;
    int sumOrig = 0;
    for (Stat s : results) {
      globalStrength += s.av - s.peer.getOriginalTotalAv();
      sumOrig += s.peer.getOriginalTotalAv();
      System.out.print(s.av + "(" + s.peer.getOriginalTotalAv() + "; " + s.peer.getReplicas().size() + ") ");
    }
    System.out.println("\nSUM OF GAINS: " + globalStrength);
    System.out.println("ORIG: " + sumOrig);
    System.out.println("OPTIMAL: " + nPeers * T);
    System.out.println("PERCENT OF GROWTH: " + (double)(globalStrength) / (nPeers * T - sumOrig));
    System.out.println("PERCENT OF COVER: " + (double)(globalStrength + sumOrig) / (nPeers * T));
  }
}