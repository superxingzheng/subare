// code by jph
package ch.ethz.idsc.subare.core.td;

import ch.ethz.idsc.subare.core.StepDigest;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.VsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** for estimating value of policy
 * using eq (6.2) on p.128
 * 
 * V(S) = V(S) + alpha * [R + gamma * V(S') - V(S)]
 * 
 * see box on p.128 */
public class TabularTemporalDifference0 implements StepDigest {
  private final VsInterface vs;
  private final Scalar gamma;
  private final Scalar alpha;

  public TabularTemporalDifference0( //
      VsInterface vs, Scalar gamma, Scalar alpha) {
    this.vs = vs;
    this.gamma = gamma;
    this.alpha = alpha;
  }

  @Override
  public final void digest(StepInterface stepInterface) {
    Tensor state0 = stepInterface.prevState();
    // action is not required
    Scalar reward = stepInterface.reward();
    Tensor state1 = stepInterface.nextState();
    // ---
    Scalar value0 = vs.value(state0);
    Scalar value1 = vs.value(state1);
    Scalar delta = reward.add(gamma.multiply(value1)).subtract(value0).multiply(alpha);
    vs.assign(state0, value0.add(delta));
  }
}
