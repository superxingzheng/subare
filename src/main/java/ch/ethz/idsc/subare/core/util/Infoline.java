// code by jph
package ch.ethz.idsc.subare.core.util;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;

// TODO not the final name... but how to name it?
public class Infoline {
  public static Infoline print(DiscreteModel discreteModel, int index, DiscreteQsa ref, DiscreteQsa qsa) {
    Infoline infoline = new Infoline(discreteModel, ref, qsa);
    System.out.println(String.format("%2d %8s  ", index, infoline.error.map(Round._1)) + infoline.loss);
    return infoline;
  }

  private final Scalar error;
  private final Scalar loss;

  public Infoline(DiscreteModel discreteModel, DiscreteQsa ref, DiscreteQsa qsa) {
    this.error = DiscreteValueFunctions.distance(qsa, ref);
    this.loss = Loss.accumulation(discreteModel, DiscreteValueFunctions.numeric(ref), qsa);
  }

  public Scalar q_error() {
    return error;
  }

  public Scalar loss() {
    return loss;
  }

  public boolean isLossfree() {
    return Chop.isZeros(loss);
  }
}
