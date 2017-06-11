// code by jph
package ch.ethz.idsc.subare.ch06.windy;

import java.awt.Dimension;
import java.awt.Point;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.util.StateActionRaster;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.Tensor;

class WindygridRaster implements StateActionRaster {
  private final Windygrid windygrid;
  private final Index indexActions;

  public WindygridRaster(Windygrid windygrid) {
    this.windygrid = windygrid;
    indexActions = Index.build(windygrid.actions);
  }

  @Override
  public DiscreteModel discreteModel() {
    return windygrid;
  }

  @Override
  public Dimension dimension() {
    return new Dimension(Windygrid.NX, (Windygrid.NY + 1) * 4 - 1);
  }

  @Override
  public Point point(Tensor state, Tensor action) {
    int sx = state.Get(0).number().intValue();
    int sy = state.Get(1).number().intValue();
    int a = indexActions.of(action);
    return new Point(sx, sy + (Windygrid.NY + 1) * a);
  }
}
