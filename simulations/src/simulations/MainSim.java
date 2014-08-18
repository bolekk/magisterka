package simulations;

import java.util.List;

public class MainSim {

  public static void main(String[] args) {
    final int nPeers = 100;
    final int T = 24;
    final int slots = 2;
    Sim sim = new Sim(nPeers, T, new CoverMeasure(), slots);
    sim.run(5);
    List<Stat> results = sim.getResults();
    int globalStrength = 0;
    int sumOrig = 0;
    for (Stat s : results) {
      globalStrength += s.av - s.peer.nAv;
      sumOrig += s.peer.nAv;
      System.out.print(s.av + "(" + s.peer.nAv + ") ");
    }
    System.out.println("\nSUM OF GAINS: " + globalStrength);
    System.out.println("ORIG: " + sumOrig);
    System.out.println("OPTIMAL: " + nPeers * T);
    System.out.println("PERCENT OF GROWTH: " + (double)(globalStrength) / (nPeers * T - sumOrig));
    System.out.println("PERCENT OF COVER: " + (double)(globalStrength + sumOrig) / (nPeers * T));
  }
}
