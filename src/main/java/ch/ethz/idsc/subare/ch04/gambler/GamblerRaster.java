// code by jph
package ch.ethz.idsc.subare.ch04.gambler;

import java.awt.Dimension;
import java.awt.Point;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.util.StateActionRaster;
import ch.ethz.idsc.tensor.Tensor;

class GamblerRaster implements StateActionRaster {
  private final Gambler gambler;
  private final int offset;

  public GamblerRaster(Gambler gambler) {
    this.gambler = gambler;
    offset = (gambler.states().length() - 1) / 2;
  }

  @Override
  public DiscreteModel discreteModel() {
    return gambler;
  }

  @Override
  public Dimension dimension() {
    int length = gambler.states().length();
    return new Dimension(length, (length + 1) / 2);
  }

  @Override
  public Point point(Tensor state, Tensor action) {
    return new Point( //
        state.Get().number().intValue(), //
        offset - action.Get().number().intValue());
  }
}
