// code by jph
package ch.ethz.idsc.subare.core.util;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.subare.core.DequeDigest;
import ch.ethz.idsc.subare.core.EpisodeDigest;
import ch.ethz.idsc.subare.core.EpisodeInterface;
import ch.ethz.idsc.subare.core.MonteCarloInterface;
import ch.ethz.idsc.subare.core.PolicyInterface;
import ch.ethz.idsc.subare.core.StepDigest;
import ch.ethz.idsc.subare.core.StepInterface;

public enum ExploringStarts {
  ;
  public static int batch(MonteCarloInterface monteCarloInterface, PolicyInterface policyInterface, //
      EpisodeDigest episodeDigest) {
    ExploringStartsBatch exploringStartBatch = new ExploringStartsBatch(monteCarloInterface);
    int episodes = 0;
    while (exploringStartBatch.hasNext()) {
      EpisodeInterface episodeInterface = exploringStartBatch.nextEpisode(policyInterface);
      episodeDigest.digest(episodeInterface);
      ++episodes;
    }
    return episodes;
  }

  public static int batchWithReplay(MonteCarloInterface monteCarloInterface, PolicyInterface policyInterface, //
      EpisodeDigest... episodeDigest) {
    List<EpisodeDigest> list = Arrays.asList(episodeDigest);
    ExploringStartsBatch exploringStartBatch = new ExploringStartsBatch(monteCarloInterface);
    int episodes = 0;
    while (exploringStartBatch.hasNext()) {
      EpisodeInterface episodeInterface = exploringStartBatch.nextEpisode(policyInterface);
      EpisodeRecording episodeRecording = new EpisodeRecording(episodeInterface);
      list.forEach(_episodeDigest -> _episodeDigest.digest(episodeRecording.replay())); // parallel
      ++episodes;
    }
    return episodes;
  }

  // TODO not used yet
  public static int batch(MonteCarloInterface monteCarloInterface, PolicyInterface policyInterface, //
      StepDigest... stepDigest) {
    List<StepDigest> list = Arrays.asList(stepDigest);
    ExploringStartsBatch exploringStartBatch = new ExploringStartsBatch(monteCarloInterface);
    int episodes = 0;
    while (exploringStartBatch.hasNext()) {
      EpisodeInterface episodeInterface = exploringStartBatch.nextEpisode(policyInterface);
      while (episodeInterface.hasNext()) {
        StepInterface stepInterface = episodeInterface.step();
        list.forEach(_stepDigest -> _stepDigest.digest(stepInterface)); // can be done in parallel!
      }
      ++episodes;
    }
    return episodes;
  }

  public static int batch(MonteCarloInterface monteCarloInterface, PolicyInterface policyInterface, int size, //
      DequeDigest... dequeDigest) {
    List<DequeDigest> list = Arrays.asList(dequeDigest);
    ExploringStartsBatch exploringStartBatch = new ExploringStartsBatch(monteCarloInterface);
    int episodes = 0;
    while (exploringStartBatch.hasNext()) {
      EpisodeInterface episodeInterface = exploringStartBatch.nextEpisode(policyInterface);
      Deque<StepInterface> deque = new LinkedList<>();
      while (episodeInterface.hasNext()) {
        deque.add(episodeInterface.step());
        if (deque.size() == size) {
          list.forEach(_dequeDigest -> _dequeDigest.digest(deque)); // TODO parallel?
          deque.poll();
        }
      }
      while (!deque.isEmpty()) {
        list.forEach(_dequeDigest -> _dequeDigest.digest(deque)); // TODO parallel?
        deque.poll();
      }
      ++episodes;
    }
    return episodes;
  }
}