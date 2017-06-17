// code by jph
package ch.ethz.idsc.subare.core.td;

import java.util.Deque;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.LearningRate;
import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.QsaInterface;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.adapter.DequeDigestAdapter;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteValueFunctions;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Multinomial;
import ch.ethz.idsc.tensor.pdf.BernoulliDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/** double sarsa for single-step, and n-step
 * 
 * implementation covers
 * 
 * Double Q-learning (box on p.145)
 * Double Expected Sarsa (Exercise 6.10 p.145)
 * Double Original Sarsa (p.145)
 * 
 * the update equation in the box uses argmax_a.
 * since there may not be a unique best action, we average the evaluation
 * over all best actions.
 * 
 * Maximization bias and Doubled learning were introduced and investigated
 * by Hado van Hasselt (2010, 2011) */
public class DoubleSarsa extends DequeDigestAdapter {
  private static final Distribution COINFLIPPING = BernoulliDistribution.of(RationalScalar.of(1, 2));
  // ---
  private final DiscreteModel discreteModel;
  private final SarsaType sarsaType;
  private final QsaInterface qsa1;
  private final QsaInterface qsa2;
  private final LearningRate learningRate1;
  private final LearningRate learningRate2;
  private Policy policy1 = null;
  private Policy policy2 = null;

  /** @param sarsaType
   * @param discreteModel
   * @param qsa1
   * @param qsa2
   * @param learningRate1
   * @param learningRate2 */
  public DoubleSarsa( //
      SarsaType sarsaType, //
      DiscreteModel discreteModel, //
      QsaInterface qsa1, //
      QsaInterface qsa2, //
      LearningRate learningRate1, //
      LearningRate learningRate2 //
  ) {
    this.discreteModel = discreteModel;
    this.sarsaType = sarsaType;
    this.qsa1 = qsa1;
    this.qsa2 = qsa2;
    this.learningRate1 = learningRate1;
    this.learningRate2 = learningRate2;
  }

  /** @param epsilon
   * @return epsilon-greedy policy with respect to (qsa1 + qsa2) / 2 */
  public Policy getEGreedy(Scalar epsilon) {
    DiscreteQsa avg = DiscreteValueFunctions.average((DiscreteQsa) qsa1, (DiscreteQsa) qsa2);
    return EGreedyPolicy.bestEquiprobable(discreteModel, avg, epsilon);
  }

  /** @param policy1 e-greedy with respect to qsa1
   * @param policy2 e-greedy with respect to qsa2 */
  public void setPolicy(Policy policy1, Policy policy2) {
    this.policy1 = policy1;
    this.policy2 = policy2;
  }

  @Override
  public void digest(Deque<StepInterface> deque) {
    // randomly select which qsa to read and write
    boolean flip = RandomVariate.of(COINFLIPPING).equals(RealScalar.ZERO); // flip coin, probability 0.5 each
    QsaInterface Qsa1 = flip ? qsa2 : qsa1; // for selecting actions and updating
    QsaInterface Qsa2 = flip ? qsa1 : qsa2; // for evaluation (of actions provided by Qsa1)
    LearningRate LearningRate1 = flip ? learningRate2 : learningRate1; // for updating
    Policy Policy1 = flip ? policy2 : policy1;
    // ---
    Tensor rewards = Tensor.of(deque.stream().map(StepInterface::reward));
    Sarsa sarsa = sarsaType.supply(discreteModel, Qsa1, null); // not used for learning
    sarsa.setPolicy(Policy1); // e-greedy with respect to qsa == Qsa1
    rewards.append(sarsa.crossEvaluate(deque.getLast().nextState(), Qsa2));
    // ---
    // the code below is identical to Sarsa
    StepInterface first = deque.getFirst();
    Tensor state0 = first.prevState(); // state-action pair that is being updated in Q
    Tensor action0 = first.action();
    Scalar value0 = Qsa1.value(state0, action0);
    Scalar gamma = discreteModel.gamma();
    Scalar alpha = LearningRate1.alpha(first);
    Scalar delta = Multinomial.horner(rewards, gamma).subtract(value0).multiply(alpha);
    Qsa1.assign(state0, action0, value0.add(delta)); // update Qsa1
    LearningRate1.digest(first); // signal to LearningRate1
  }
}
