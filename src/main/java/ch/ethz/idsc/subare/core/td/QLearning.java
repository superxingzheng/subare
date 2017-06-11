// code by jph
package ch.ethz.idsc.subare.core.td;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.QsaInterface;
import ch.ethz.idsc.subare.core.util.DiscreteUtils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Q-learning: An off-policy TD control algorithm
 * 
 * eq (6.8)
 * 
 * box on p.140
 * 
 * see also Watkins 1989 */
public class QLearning extends ActionSarsa {
  /** @param discreteModel
   * @param qsa
   * @param alpha learning rate should converge to zero, with
   * sum of alpha's should go to infinity, sum of alpha's squared should be finite */
  public QLearning(DiscreteModel discreteModel, QsaInterface qsa, Scalar alpha) {
    super(discreteModel, qsa, alpha);
  }

  @Override
  Tensor selectAction(Tensor state) {
    return DiscreteUtils.fairBestAction(discreteModel, qsa, state); // ArgMax_a Q(S,a)
  }
}
