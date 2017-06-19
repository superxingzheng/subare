// code by jph
package ch.ethz.idsc.subare.core.util;

import java.awt.Point;

import ch.ethz.idsc.tensor.Tensor;

public interface StateRaster extends Raster {
  /** @param state
   * @return point with x, y as coordinates of state in raster,
   * or null if state does not have a position in the raster */
  Point point(Tensor state);

  // TODO use!
  int magify();
}
