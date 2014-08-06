package simulations;

import java.util.List;

public class MainSim {
  public static void main(String[] args) {
    Sim sim = new Sim(100, 24);
    sim.run(5);
    List<Stat> results = sim.getResults();
    for (Stat s : results) {
      System.out.print(s.av);
      System.out.print(" ");
    }
  }
}
