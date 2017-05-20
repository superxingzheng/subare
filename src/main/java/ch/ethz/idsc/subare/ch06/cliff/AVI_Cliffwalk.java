// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch06.cliff;

import java.io.IOException;

import ch.ethz.idsc.subare.core.alg.ActionValueIteration;
import ch.ethz.idsc.subare.core.util.DiscreteUtils;
import ch.ethz.idsc.subare.core.util.DiscreteVs;
import ch.ethz.idsc.subare.core.util.GreedyPolicy;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Export;

/** */
class AVI_Cliffwalk {
  public static void main(String[] args) throws IOException {
    Cliffwalk cliffwalk = new Cliffwalk();
    ActionValueIteration avi = new ActionValueIteration(cliffwalk, cliffwalk, RealScalar.ONE);
    avi.untilBelow(DecimalScalar.of(.0001));
    DiscreteVs dvs = DiscreteUtils.createVs(cliffwalk, avi.qsa());
    Export.of(UserHome.file("cliffwalk_qsa_avi.png"), CliffwalkHelper.render(cliffwalk, avi.qsa()));
    dvs.print();
    GreedyPolicy greedyPolicy = GreedyPolicy.bestEquiprobableGreedy(cliffwalk, avi.qsa());
    greedyPolicy.print(cliffwalk.states());
  }
}
