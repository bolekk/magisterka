package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peer.CorpProbPeerFactory;
import peer.CorpTimeSlotPeerFactory;
import peer.NapsterPeerFactory;
import peer.Peer;
import peer.PeerFactory;
import peer.UniformContPeerFactory;
import peer.UniformPeerFactory;
import simulation.GameSimulation;
import simulation.RandomizedSimulation;
import simulation.Simulation;
import measure.peer.Measure;
import measure.peer.PureStrengthMeasure;
import measure.peer.PureWeaknessMeasure;
import measure.peer.SelfishPrivateMeasure;
import measure.peer.StrengthBiasedMeasure;
import measure.peer.SumMeasure;
import measure.peer.WeakBiasedMeasure;
import measure.sys.BasicSystemMeasure;
import measure.sys.EquitableSystemMeasure;
import measure.sys.SystemMeasure;

/**
 * Application entry point.
 */
public class Main {

  private static final int maxIters = 30;
  private static final double epsilon = 0.0005;
  private static final Random random = new Random(121314312);
  
  private static final int nPeers = 1000;
  private static final int timeSlots = 24;
  private static final int replicationSlots = 5;


  public static void main(String[] args) {
    List<PeerFactory> factories = new ArrayList<>();    
    //final int expectedCoverage = 4;
    //factories.add(new UniformPeerFactory(expectedCoverage, random.nextLong()));
    //factories.add(new UniformContPeerFactory(expectedCoverage, random.nextLong()));
    //factories.add(new NapsterPeerFactory(random.nextLong()));
    //factories.add(new CorpProbPeerFactory(random.nextLong()));
    final double paretoShape = 0.1;
    factories.add(new CorpTimeSlotPeerFactory(paretoShape, random.nextLong()));
    
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
        Simulation sim = new GameSimulation(peers);
        runAndPrint(factory.getName(), measure.getName(), sim, sysMeasures);
      }
      // random (measure is irrelevant)
      List<Peer> peers = factory.generatePeers(nPeers, timeSlots, replicationSlots, new SumMeasure(), new SumMeasure()); 
      Simulation sim = new RandomizedSimulation(peers, random.nextLong());
      runAndPrint(factory.getName(), "Random", sim, sysMeasures);
    }
  }

  private static void runAndPrint(String factoryName, String peerMeasureName, Simulation sim, List<SystemMeasure> sysMeasures) {
    SystemMeasure firstMeasure = sysMeasures.get(0);
    double lastVal = 2.0;
    int iter = 0;
    for (; iter < maxIters; iter++) {
      sim.run(1, true);
      double currVal = firstMeasure.evaluateNormalized(sim);
      if (Math.abs(currVal - lastVal) < epsilon) {
        break;
      }
      lastVal = currVal;
    }
    
    System.out.println("---- FACTORY: " + factoryName + " ---- PEER MEASURE: " + peerMeasureName + " ---- ITERATIONS: " + iter);
    for (SystemMeasure sysMeasure : sysMeasures) {
      System.out.println("SYS MEASURE (" + sysMeasure.getName() + ") = " + sysMeasure.evaluateNormalized(sim));
    }
  }
}