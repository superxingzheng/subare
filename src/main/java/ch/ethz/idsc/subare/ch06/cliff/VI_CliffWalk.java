// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch06.cliff;

import ch.ethz.idsc.subare.core.EpisodeInterface;
import ch.ethz.idsc.subare.core.PolicyInterface;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.alg.ValueIteration;
import ch.ethz.idsc.subare.core.util.GreedyPolicy;
import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** */
class VI_CliffWalk {
  public static void main(String[] args) {
    CliffWalk cliffWalk = new CliffWalk();
    ValueIteration vi = new ValueIteration(cliffWalk, RealScalar.ONE);
    vi.untilBelow(DecimalScalar.of(.0001));
    // GreedyPolicy greedyPolicy = GreedyPolicy.bestEquiprobableGreedy(cliffWalk, values);
    // greedyPolicy.print(cliffWalk.states());
    // Index statesIndex = Index.build(cliffWalk.states());
    // for (int stateI = 0; stateI < statesIndex.size(); ++stateI) {
    // Tensor state = statesIndex.get(stateI);
    // System.out.println(state + " " + values.get(stateI).map(ROUND));
    // }
    PolicyInterface policyInterface = GreedyPolicy.bestEquiprobableGreedy(cliffWalk, vi.vs().values());
    EpisodeInterface mce = cliffWalk.kickoff(policyInterface);
    while (mce.hasNext()) {
      StepInterface stepInterface = mce.step();
      Tensor state = stepInterface.prevState();
      System.out.println(state + " then " + stepInterface.action());
    }
  }
}
