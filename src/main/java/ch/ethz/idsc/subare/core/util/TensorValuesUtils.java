// code by jph
package ch.ethz.idsc.subare.core.util;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.LogisticSigmoid;

public enum TensorValuesUtils {
  ;
  private static final Scalar HALF = RationalScalar.of(1, 2);

  // ---
  @SuppressWarnings("unchecked")
  public static <T extends TensorValuesInterface> T rescaled(T tvi) {
    return (T) tvi.create(Rescale.of(tvi.values()).flatten(0));
  }

  public static Scalar distance(TensorValuesInterface tvi1, TensorValuesInterface tvi2) {
    return Norm._1.of(_difference(tvi1, tvi2));
  }

  @SuppressWarnings("unchecked")
  public static <T extends TensorValuesInterface> T average(T tvi1, T tvi2) {
    return (T) tvi1.create(tvi1.values().add(tvi2.values()).multiply(HALF).flatten(0));
  }

  @SuppressWarnings("unchecked")
  public static <T extends TensorValuesInterface> T logisticDifference(T tvi1, T tvi2) {
    return (T) tvi1.create(LogisticSigmoid.of(_difference(tvi1, tvi2)).flatten(0));
  }

  @SuppressWarnings("unchecked")
  public static <T extends TensorValuesInterface> T logisticDifference(T tvi1, T tvi2, Scalar factor) {
    return (T) tvi1.create(LogisticSigmoid.of(_difference(tvi1, tvi2).multiply(factor)).flatten(0));
  }

  // helper function
  private static boolean _isCompatible(TensorValuesInterface tvi1, TensorValuesInterface tvi2) {
    return tvi1.keys().equals(tvi2.keys());
  }

  // helper function
  private static Tensor _difference(TensorValuesInterface tvi1, TensorValuesInterface tvi2) {
    if (!_isCompatible(tvi1, tvi2))
      throw new RuntimeException();
    return tvi1.values().subtract(tvi2.values());
  }
}