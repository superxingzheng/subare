// code by jph
package ch.ethz.idsc.subare.util;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class Average {
  private Tensor average = null;
  private Scalar count = RealScalar.ZERO;

  /** @param tensor that contributes to the average of all tracked {@link Tensor}s */
  public void track(Tensor tensor) {
    if (Scalars.isZero(count)) { // <- previous count is required here
      count = count.add(RealScalar.ONE);
      average = tensor.copy();
    } else {
      count = count.add(RealScalar.ONE);
      Scalar weight = count.invert();
      average = average.multiply(RealScalar.ONE.subtract(weight)) //
          .add(tensor.multiply(weight));
    }
  }

  /** @return average of {@link Tensor}s trackked by {@link #track(Tensor)},
   * or null if function {@link #track(Tensor)} has not been called. */
  public Tensor get() {
    return average;
  }

  /** @return {@link #get()} cast to {@link Scalar} */
  public Scalar Get() {
    return (Scalar) average;
  }

  @Override
  public String toString() {
    return average.toString();
  }
}
