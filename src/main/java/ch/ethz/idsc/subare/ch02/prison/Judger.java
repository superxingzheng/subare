// code by jph
package ch.ethz.idsc.subare.ch02.prison;

import ch.ethz.idsc.subare.ch02.Agent;
import ch.ethz.idsc.subare.util.GlobalAssert;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

class Judger {
  final Tensor reward;
  final Agent a1;
  final Agent a2;

  Judger(Tensor r1, Agent a1, Agent a2) {
    this.reward = r1;
    this.a1 = a1;
    this.a2 = a2;
  }

  void play() {
    int A1 = a1.takeAction();
    int A2 = a2.takeAction();
    RealScalar r1 = (RealScalar) reward.Get(A1, A2);
    RealScalar r2 = (RealScalar) reward.Get(A2, A1);
    // System.out.println("action " + k1 + " r=" + r1 + " " + k2 + " r=" + r2);
    // RealScalar alpha = RationalScalar.of(1, 2);
    // a1.feedReward(A1, (RealScalar) r1.plus(alpha.multiply(r2)));
    // a2.feedReward(A2, (RealScalar) r2.plus(alpha.multiply(r1)));
    a1.feedReward(A1, r1);
    a2.feedReward(A2, r2);
  }

  void ranking() {
    GlobalAssert.of(a1.getCount() == a2.getCount());
    // System.out.println("c1 "+a1.getCount());
    // System.out.println("c2 "+a2.getCount());
    final RealScalar div = RationalScalar.of(1, a1.getCount());
    RealScalar g1 = (RealScalar) a1.getTotal().multiply(div);
    RealScalar g2 = (RealScalar) a2.getTotal().multiply(div);
    System.out.println(String.format("%s    %6.3f", a1.getAbsDesc(), g1.getRealDouble()));
    System.out.println(String.format("%s    %6.3f", a2.getAbsDesc(), g2.getRealDouble()));
  }
}