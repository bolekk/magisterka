package measure.sys;

import simulation.Simulation;

public abstract class SystemMeasure {

  public abstract double evaluate(Simulation simulation);

  public abstract double evaluateNormalized(Simulation simulation);

  public abstract String getName();
}
