// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch06.cliff;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.alg.ActionValueIteration;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteUtils;
import ch.ethz.idsc.subare.core.util.DiscreteValueFunctions;
import ch.ethz.idsc.subare.core.util.DiscreteVs;
import ch.ethz.idsc.subare.core.util.GreedyPolicy;
import ch.ethz.idsc.subare.core.util.Policies;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.GifSequenceWriter;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Round;

/** action value iteration for cliff walk */
class AVI_Cliffwalk {
  public static void main(String[] args) throws Exception {
    Cliffwalk cliffwalk = new Cliffwalk(12, 4);
    DiscreteQsa ref = CliffwalkHelper.getOptimalQsa(cliffwalk);
    Export.of(UserHome.Pictures("cliffwalk_qsa_avi.png"), //
        CliffwalkHelper.render(cliffwalk, DiscreteValueFunctions.rescaled(ref)));
    ActionValueIteration avi = new ActionValueIteration(cliffwalk);
    GifSequenceWriter gsw = GifSequenceWriter.of(UserHome.Pictures("cliffwalk_qsa_avi.gif"), 200);
    for (int index = 0; index < 20; ++index) {
      Scalar error = DiscreteValueFunctions.distance(avi.qsa(), ref);
      System.out.println(index + " " + error.map(Round._1));
      gsw.append(ImageFormat.of(CliffwalkHelper.joinAll(cliffwalk, avi.qsa(), ref)));
      avi.step();
    }
    gsw.append(ImageFormat.of(CliffwalkHelper.joinAll(cliffwalk, avi.qsa(), ref)));
    gsw.close();
    DiscreteVs vs = DiscreteUtils.createVs(cliffwalk, ref);
    vs.print();
    Policy policy = GreedyPolicy.bestEquiprobable(cliffwalk, ref);
    Policies.print(policy, cliffwalk.states());
  }
}
