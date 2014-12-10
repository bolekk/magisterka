package measure.sys;

import simulation.Sim;

public abstract class SystemMeasure {

  public abstract double evaluate(Sim simulation);
  
  public abstract double evaluateNormalized(Sim simulation);
  
  public abstract String getName();
}
