package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peer.CorpUniformPeerFactory;
import peer.CorpParetoPeerFactory;
import peer.NapsterPeerFactory;
import peer.Peer;
import peer.PeerFactory;
import peer.UniformContPeerFactory;
import peer.UniformPeerFactory;
import simulation.GameSimulation;
import simulation.RandomizedSimulation;
import simulation.Simulation;
import measure.peer.Measure;
import measure.peer.StrongMeasure;
import measure.peer.WeakMeasure;
import measure.peer.SelfishPrivateMeasure;
import measure.peer.HalfStrongMeasure;
import measure.peer.SumMeasure;
import measure.peer.HalfWeakMeasure;
import measure.sys.BasicSystemMeasure;
import measure.sys.EquitableSystemMeasure;
import measure.sys.SystemMeasure;

/**
 * Application entry point.
 * 
 * @author Boleslaw Kulbabinski <bk277531@students.mimuw.edu.pl>
 */
public class Main {

  private static final int maxIters = 30;
  private static final double epsilon = 0.0005;
  private static final Random random = new Random(System.currentTimeMillis());

  private static final int nPeers = 1000;
  private static final int timeSlots = 24;
  private static final int replicationSlots = 5;

  public static void main(String[] args) {
    /*
     * Factories used in the experiment.
     */
    List<PeerFactory> factories = new ArrayList<>();
    final int expectedCoverage = 4;
    factories.add(new UniformPeerFactory(expectedCoverage, random.nextLong()));
    factories.add(new UniformContPeerFactory(expectedCoverage, random
        .nextLong()));
    factories.add(new NapsterPeerFactory(random.nextLong()));
    factories.add(new CorpUniformPeerFactory(random.nextLong()));
    final double paretoShape = 0.1;
    factories.add(new CorpParetoPeerFactory(paretoShape, random.nextLong()));

    /*
     * Acceptance measures used in the experiment.
     */
    List<Measure> measures = new ArrayList<>();
    measures.add(new WeakMeasure());
    measures.add(new HalfWeakMeasure());
    measures.add(new SumMeasure());
    measures.add(new HalfStrongMeasure());
    measures.add(new StrongMeasure());

    /*
     * System measures used in the experiment.
     */
    List<SystemMeasure> sysMeasures = new ArrayList<>();
    sysMeasures.add(new BasicSystemMeasure(false));
    sysMeasures.add(new EquitableSystemMeasure(false));

    /*
     * Private measure is constant.
     */
    Measure privateMeasure = new SelfishPrivateMeasure();

    for (PeerFactory factory : factories) {
      // games
      for (Measure measure : measures) {
        List<Peer> peers = factory.generatePeers(nPeers, timeSlots,
            replicationSlots, privateMeasure, measure);
        Simulation sim = new GameSimulation(peers);
        runAndPrint(factory.getName(), measure.getName(), sim, sysMeasures);
      }
      // random (measure is irrelevant)
      List<Peer> peers = factory.generatePeers(nPeers, timeSlots,
          replicationSlots, new SumMeasure(), new SumMeasure());
      Simulation sim = new RandomizedSimulation(peers, random.nextLong());
      runAndPrint(factory.getName(), "Random", sim, sysMeasures);
    }
  }

  private static void runAndPrint(String factoryName, String peerMeasureName,
      Simulation sim, List<SystemMeasure> sysMeasures) {
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

    System.out.println("---- FACTORY: " + factoryName + " ---- PEER MEASURE: "
        + peerMeasureName + " ---- ITERATIONS: " + iter);
    for (SystemMeasure sysMeasure : sysMeasures) {
      System.out.println("SYS MEASURE (" + sysMeasure.getName() + ") = "
          + sysMeasure.evaluateNormalized(sim));
    }
  }
}
