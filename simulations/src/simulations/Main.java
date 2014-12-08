package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import measure.sys.BasicSystemMeasure;
import measure.sys.EquitableSystemMeasure;
import measure.sys.SystemMeasure;

public class Main {

  private static final int nPeers = 1000;
  private static final int timeSlots = 24;
  private static final int replicationSlots = 5;
  private static final int maxIters = 25;
  private static final double epsilon = 0.001;
  private static final Random random = new Random(121314312);



  public static void main(String[] args) {
    List<PeerFactory> factories = new ArrayList<>();    
    final int expectedCoverage = 4;
    //factories.add(new UniformPeerFactory(expectedCoverage));
    factories.add(new UniformContPeerFactory(expectedCoverage, random.nextLong()));
    //factories.add(new NapsterPeerFactory());
    //factories.add(new KrzProbPeerFactory());
    //final double paretoShape = 0.1;
    //factories.add(new KrzTimeSlotPeerFactory(paretoShape));
    
    Measure privateMeasure = new SelfishPrivateMeasure();

    List<Measure> measures = new ArrayList<>();
    measures.add(new PureWeaknessMeasure());
    measures.add(new WeakBiasedMeasure());
    measures.add(new SumMeasure());
    measures.add(new StrengthBiasedMeasure());
    measures.add(new PureStrengthMeasure());
    
    List<SystemMeasure> sysMeasures = new ArrayList<>();
    sysMeasures.add(new BasicSystemMeasure(false));
    sysMeasures.add(new EquitableSystemMeasure(false));

    for (PeerFactory factory : factories) {
      // games
      for (Measure measure : measures) {
        List<Peer> peers = factory.generatePeers(nPeers, timeSlots, replicationSlots, privateMeasure, measure);
        Sim sim = new GameSim(peers);
        runAndPrint(factory.getName(), measure.getName(), sim, sysMeasures);
      }
      // random (measure irrelevant)
      List<Peer> peers = factory.generatePeers(nPeers, timeSlots, replicationSlots, new SumMeasure(), new SumMeasure()); 
      Sim sim = new RandomSim(peers);
      runAndPrint(factory.getName(), "Random", sim, sysMeasures);
    }
  }

  private static void runAndPrint(String factoryName, String peerMeasureName, Sim sim, List<SystemMeasure> sysMeasures) {
    int effectiveIters = sim.run(maxIters, true);
    
    System.out.println("---- FACTORY: " + factoryName + " ---- PEER MEASURE: " + peerMeasureName);
    for (SystemMeasure sysMeasure : sysMeasures) {
      printAllResults(sim, effectiveIters, sysMeasure);
    }
  }

  private static void printAllResults(Sim sim, int effectiveIters, SystemMeasure sysMeasure) {
    System.out.println("SYS MEASURE (" + sysMeasure.getName() + ") = " + sysMeasure.evaluateNormalized(sim));
  }
}