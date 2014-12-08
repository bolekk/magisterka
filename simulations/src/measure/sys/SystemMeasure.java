package measure.sys;

import simulations.Sim;

public abstract class SystemMeasure {

  public abstract double evaluate(Sim simulation);
  
  public abstract double evaluateNormalized(Sim simulation);
  
  public abstract String getName();
}
