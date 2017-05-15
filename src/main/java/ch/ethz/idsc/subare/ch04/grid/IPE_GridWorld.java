// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch04.grid;

import ch.ethz.idsc.subare.core.alg.IterativePolicyEvaluation;
import ch.ethz.idsc.subare.core.util.EquiprobablePolicy;
import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Round;

/** determines value function for equiprobable "random" policy
 * 
 * produces results on p.83:
 * 
 * {0, 0} 0
 * {0, 1} -14.0
 * {0, 2} -20.0
 * {0, 3} -22.0
 * {1, 0} -14.0
 * {1, 1} -18.0
 * {1, 2} -20.0
 * {1, 3} -20.0
 * {2, 0} -20.0
 * {2, 1} -20.0
 * {2, 2} -18.0
 * {2, 3} -14.0
 * {3, 0} -22.0
 * {3, 1} -20.0
 * {3, 2} -14.0
 * {3, 3} 0 */
class IPE_GridWorld {
  public static void main(String[] args) {
    GridWorld gridWorld = new GridWorld();
    IterativePolicyEvaluation ipe = new IterativePolicyEvaluation( //
        gridWorld, new EquiprobablePolicy(gridWorld), RealScalar.ONE);
    ipe.until(DecimalScalar.of(.0001));
    ipe.vs().print(Round.toMultipleOf(DecimalScalar.of(.1)));
  }
}