// code by jph
package ch.ethz.idsc.subare.core.util;

import java.util.function.Function;
import java.util.stream.Stream;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.QsaInterface;
import ch.ethz.idsc.subare.core.VsInterface;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;

public class DiscreteVs implements VsInterface {
  /** initializes all state value to zero
   * 
   * @param discreteModel
   * @return */
  public static DiscreteVs build(DiscreteModel discreteModel) {
    Index index = Index.build(discreteModel.states());
    return new DiscreteVs(index, Array.zeros(index.size()));
  }

  public static DiscreteVs build(DiscreteModel discreteModel, Tensor values) {
    return new DiscreteVs(Index.build(discreteModel.states()), values);
  }

  // TODO function does not belong here
  public static DiscreteVs create(DiscreteModel discreteModel, QsaInterface qsa) {
    return build(discreteModel, //
        Tensor.of(discreteModel.states().flatten(0) //
            .map(state -> discreteModel.actions(state).flatten(0) //
                .map(action -> qsa.value(state, action)) //
                .reduce(Max::of).get()))); // <- assumes greedy policy
  }

  private final Index index;
  private final Tensor values;

  /** @param index
   * @param values */
  public DiscreteVs(Index index, Tensor values) {
    if (index.size() != values.length())
      throw new RuntimeException();
    this.index = index;
    this.values = values;
  }

  @Override
  public Scalar value(Tensor state) {
    return values.Get(index.of(state));
  }

  @Override
  public void assign(Tensor state, Scalar value) {
    values.set(value, index.of(state));
  }

  public DiscreteVs create(Stream<Tensor> stream) {
    return new DiscreteVs(index, Tensor.of(stream));
  }

  @Override
  public DiscreteVs copy() {
    return new DiscreteVs(index, values.copy());
  }

  @Override
  public DiscreteVs discounted(Scalar gamma) {
    return new DiscreteVs(index, values.multiply(gamma));
  }

  public Tensor values() {
    return values.unmodifiable();
  }

  public void print() {
    print(Function.identity());
  }

  public void print(Function<Scalar, Scalar> ROUND) {
    for (Tensor key : index.keys()) {
      Scalar value = values.Get(index.of(key));
      System.out.println(key + " " + value.map(ROUND));
    }
  }

  @Override
  public Scalar distance(VsInterface vs) {
    return Norm._1.of(values.subtract(((DiscreteVs) vs).values));
  }
}
