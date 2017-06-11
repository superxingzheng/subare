// code by jph
package ch.ethz.idsc.subare.ch06.maxbias;

import ch.ethz.idsc.subare.core.alg.ActionValueIteration;
import ch.ethz.idsc.subare.core.alg.Random1StepTabularQPlanning;
import ch.ethz.idsc.subare.core.util.ActionValueStatistics;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.TabularSteps;
import ch.ethz.idsc.subare.core.util.TensorValuesUtils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

class RSTQP_Maxbias {
  public static void main(String[] args) {
    Maxbias maxbias = new Maxbias(3);
    DiscreteQsa ref = MaxbiasHelper.getOptimalQsa(maxbias);
    DiscreteQsa qsa = DiscreteQsa.build(maxbias);
    Random1StepTabularQPlanning rstqp = new Random1StepTabularQPlanning(maxbias, qsa);
    rstqp.setLearningRate(RealScalar.of(1.));
    ActionValueStatistics avs = new ActionValueStatistics(maxbias);
    for (int index = 0; index < 500; ++index)
      TabularSteps.batch(maxbias, maxbias, rstqp, avs);
    {
      Scalar error = TensorValuesUtils.distance(ref, qsa);
      System.out.println("r1s error=" + error);
    }
    System.out.println("---");
    ActionValueIteration avi = new ActionValueIteration(maxbias, avs);
    avi.untilBelow(RealScalar.of(.0001));
    avi.qsa().print();
    {
      Scalar error = TensorValuesUtils.distance(ref, avi.qsa());
      System.out.println("avs error=" + error);
    }
  }
}