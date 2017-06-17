// code by jph
package ch.ethz.idsc.subare.ch04.gambler;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.mc.MonteCarloExploringStarts;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteUtils;
import ch.ethz.idsc.subare.core.util.DiscreteVs;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.subare.core.util.Infoline;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.GifSequenceWriter;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Round;

class MCES_Gambler {
  public static void main(String[] args) throws Exception {
    Gambler gambler = Gambler.createDefault();
    final DiscreteQsa ref = GamblerHelper.getOptimalQsa(gambler);
    MonteCarloExploringStarts mces = new MonteCarloExploringStarts(gambler);
    GifSequenceWriter gsw = GifSequenceWriter.of(UserHome.Pictures("gambler_qsa_mces.gif"), 200);
    int EPISODES = 20;
    for (int index = 0; index < EPISODES; ++index) {
      Infoline.print(gambler, index, ref, mces.qsa());
      Policy policy = //
          EGreedyPolicy.bestEquiprobable(gambler, mces.qsa(), RealScalar.of(.1));
      ExploringStarts.batch(gambler, policy, mces);
      gsw.append(ImageFormat.of(GamblerHelper.qsaPolicyRef(gambler, mces.qsa(), ref)));
    }
    gsw.close();
    DiscreteVs discreteVs = DiscreteUtils.createVs(gambler, mces.qsa());
    discreteVs.print(Round._2);
  }
}
