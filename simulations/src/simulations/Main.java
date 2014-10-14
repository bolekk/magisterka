package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

  private static Random rand = new Random(122413);
  
  public static void main(String[] args) {
    final int nPeers = 500;
    final int T = 24;
    final int slots = 2;
    final int maxIters = 50;

    List<Peer> peers = initPeers(nPeers, T, slots, new SumMeasure());
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
    System.out.println("TOTAL NUMBER OF SLOTS: " + nPeers * slots);
    System.out.println("TOTAL USED SLOTS: " + usedSlots);
    System.out.println("SUM OF GAINS: " + globalStrength);
    System.out.println("ORIG: " + sumOrig);
    System.out.println("OPTIMAL: " + nPeers * T);
    System.out.println("PERCENT OF GROWTH: " + (double)(globalStrength) / (nPeers * T - sumOrig));
    System.out.println("PERCENT OF COVER: " + (double)(globalStrength + sumOrig) / (nPeers * T));
  }


  private static List<Peer> initPeers(int nPeers, int T, int slots, Measure measure) {
    List<Peer> peers = new ArrayList<>();
    for (int i = 0; i < nPeers; ++i) {
      peers.add(new Peer(i, T, getRandomAv(T, 8), slots, measure));
    }
    return peers;
  }

  private static List<Boolean> getRandomAv(int len, int expected) {
    List<Boolean> ret = new ArrayList<>();
    for (int i = 0; i < len; ++i) {
      int nextInt = rand.nextInt(len);
      ret.add(nextInt < expected);
    }
    return ret;
  }
}