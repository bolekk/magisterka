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
import measure.sys.SystemMeasure;

/**
 * Application entry point.
 * 
 * @author Boleslaw Kulbabinski <bk277531@students.mimuw.edu.pl>
 */
public class Main {

  private static final int maxIters = 30;
  private static final double epsilon = 0.0005;
  private static final int runs = 10;
  private static final Random random = new Random(System.currentTimeMillis());

  private static final int nPeers = 1000;
  private static final int timeSlots = 24;
  private static final int replicationSlots = 3;

  public static void main(String[] args) {
    /*
     * Factories used in the experiment.
     */
    List<PeerFactory> factories = new ArrayList<>();
    final int expectedCoverage = 8;
    factories.add(new UniformPeerFactory(expectedCoverage, random.nextLong()));
    factories.add(new UniformContPeerFactory(expectedCoverage, random.nextLong()));
    factories.add(new NapsterPeerFactory(random.nextLong()));
    factories.add(new CorpUniformPeerFactory(random.nextLong()));
    final double paretoShape = 1.0;
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
     * System measure used in the experiment.
     */
    SystemMeasure sysMeasure = new BasicSystemMeasure(false);
    // SystemMeasure sysMeasure = new EquitableSystemMeasure(false);

    /*
     * Private measure is constant.
     */
    Measure privateMeasure = new SelfishPrivateMeasure();

    for (PeerFactory factory : factories) {
      // games
      for (Measure measure : measures) {
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
          List<Peer> peers = factory.generatePeers(nPeers, timeSlots,
              replicationSlots, privateMeasure, measure);
          Simulation sim = new GameSimulation(peers);
          results.add(runSingle(sim, sysMeasure));
        }
        printResults(results, factory.getName(), measure.getName(), sysMeasure.getName());
      }
      // random (measure is irrelevant)
      List<Result> results = new ArrayList<>();
      for (int i = 0; i < runs; i++) {
        List<Peer> peers = factory.generatePeers(nPeers, timeSlots,
            replicationSlots, new SumMeasure(), new SumMeasure());
        Simulation sim = new RandomizedSimulation(peers, random.nextLong());
        results.add(runSingle(sim, sysMeasure));
      }
      printResults(results, factory.getName(), "Random", sysMeasure.getName());
    }
  }

  private static Result runSingle(Simulation sim, SystemMeasure sysMeasure) {
    Result res = new Result();
    double lastVal = 2.0;
    int iter = 0;
    for (; iter < maxIters; iter++) {
      sim.run(1, true);
      double currVal = sysMeasure.evaluateNormalized(sim);
      if (Math.abs(currVal - lastVal) < epsilon) {
        break;
      }
      lastVal = currVal;
    }
    res.iters = iter;
    res.score = sysMeasure.evaluateNormalized(sim);
    return res;
  }
  
  private static void printResults(List<Result> results, String factoryName, String peerMeasureName,
      String sysMeasureName) {
    List<Double> scores = new ArrayList<>();
    List<Double> iters = new ArrayList<>();
    for (Result res : results) {
      scores.add(res.score);
      iters.add((double)res.iters);
    }
    System.out.println("-- FACTORY: " + factoryName + " ---- PEER MEASURE: " + peerMeasureName);
    System.out.println("     SYS MEASURE (" + sysMeasureName + "): Average: " + getAverage(scores) +
        " Std Deviation: " + getStdDeviation(scores));
    System.out.println("     ITERATIONS: Average: " + getAverage(iters) + " Std Deviation: " +
        getStdDeviation(iters));
  }

  private static double getStdDeviation(List<Double> results) {
    double mean = getAverage(results);
    double res = 0.0;
    for (Double d : results) {
      res += Math.pow(mean - d, 2.0);
    }
    return Math.sqrt(res / results.size());
  }

  private static double getAverage(List<Double> results) {
    double res = 0.0;
    for (Double d : results) {
      res += d;
    }
    return res / results.size();
  }

  static class Result {
    public int iters;
    public double score;
  }
}
