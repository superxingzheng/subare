// code by jph
package ch.ethz.idsc.subare.ch06.cliff;

import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.util.ImageResize;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.subare.util.color.Colorscheme;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.opt.Interpolation;

enum CliffwalkHelper {
  ;
  private static final Tensor BASE = Tensors.vector(255);

  static Tensor render(Cliffwalk cliffwalk, DiscreteQsa qsa) {
    Interpolation colorscheme = Colorscheme.classic();
    final Tensor tensor = Array.zeros(Cliffwalk.NX, Cliffwalk.NY * 4, 4);
    Index indexActions = Index.build(cliffwalk.actions);
    DiscreteQsa scaled = qsa.create(Rescale.of(qsa.values()).flatten(0));
    for (Tensor state : cliffwalk.states())
      for (Tensor action : cliffwalk.actions(state)) {
        Scalar sca = scaled.value(state, action);
        int sx = state.Get(0).number().intValue();
        int sy = state.Get(1).number().intValue();
        int a = indexActions.of(action);
        tensor.set(colorscheme.get(BASE.multiply(sca)), sx, sy + Cliffwalk.NY * a);
      }
    return ImageResize.of(tensor, 7);
  }
}
