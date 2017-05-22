// code by jph
package ch.ethz.idsc.subare.core.nstep;

import java.util.LinkedList;

import ch.ethz.idsc.subare.core.EpisodeInterface;
import ch.ethz.idsc.subare.core.EpisodeSupplier;
import ch.ethz.idsc.subare.core.PolicyInterface;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.VsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Multinomial;

/** n-step temporal difference for estimating V(s)
 * 
 * box on p. 154 */
// TODO not tested yet
public class NStepTemporalDifference {
  private final EpisodeSupplier episodeSupplier;
  private final PolicyInterface policyInterface;
  private final VsInterface vs;
  private final Scalar gamma;
  private final Scalar alpha;
  private final int size;

  public NStepTemporalDifference( //
      EpisodeSupplier episodeSupplier, PolicyInterface policyInterface, //
      VsInterface vs, Scalar gamma, Scalar alpha, int size) {
    this.episodeSupplier = episodeSupplier;
    this.policyInterface = policyInterface;
    this.vs = vs;
    this.gamma = gamma;
    this.alpha = alpha;
    this.size = size;
  }

  public void simulate() {
    EpisodeInterface episodeInterface = episodeSupplier.kickoff(policyInterface);
    LinkedList<StepInterface> list = new LinkedList<>();
    while (episodeInterface.hasNext()) {
      StepInterface stepInterface = episodeInterface.step();
      list.add(stepInterface);
      if (size == list.size()) {
        StepInterface last = list.getLast();
        Tensor rewards = Tensor.of(list.stream().map(StepInterface::reward));
        rewards.append(vs.value(last.nextState()));
        Scalar G = Multinomial.horner(rewards, gamma);
        StepInterface first = list.getFirst();
        Scalar value = vs.value(first.prevState());
        vs.assign(first.prevState(), value.add(G.subtract(value).multiply(alpha)));
        list.removeFirst();
      }
    }
    while (!list.isEmpty()) {
      Tensor rewards = Tensor.of(list.stream().map(StepInterface::reward));
      Scalar G = Multinomial.horner(rewards, gamma);
      StepInterface first = list.getFirst();
      Scalar value = vs.value(first.prevState());
      vs.assign(first.prevState(), value.add(G.subtract(value).multiply(alpha)));
      list.removeFirst();
    }
  }
}