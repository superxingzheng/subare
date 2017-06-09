// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch06.walk;

import ch.ethz.idsc.subare.core.PolicyInterface;
import ch.ethz.idsc.subare.core.td.Sarsa;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.EquiprobablePolicy;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Round;

/** determines state action value function q(s,a).
 * initial policy is irrelevant because each state allows only one action.
 * 
 * {0, 0} 0
 * {1, 0} 0.16
 * {2, 0} 0.35
 * {3, 0} 0.47
 * {4, 0} 0.59
 * {5, 0} 0.79
 * {6, 0} 0 */
class SD_Randomwalk {
  static void handle(SarsaType type) {
    System.out.println(type);
    Randomwalk randomwalk = new Randomwalk();
    DiscreteQsa qsa = DiscreteQsa.build(randomwalk);
    PolicyInterface policyInterface = new EquiprobablePolicy(randomwalk);
    Sarsa sarsa = type.supply(randomwalk, qsa, RealScalar.of(.1), policyInterface);
    for (int count = 0; count < 1000; ++count)
      ExploringStarts.batch(randomwalk, policyInterface, 4, sarsa); // sarsa, 4
    qsa.print(Round.toMultipleOf(DecimalScalar.of(.01)));
  }

  public static void main(String[] args) {
    for (SarsaType type : SarsaType.values())
      handle(type);
  }
}
