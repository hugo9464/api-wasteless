package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteOverviewInterface {
  /**
   * For each waste type, the waste weight registered by instant.
   *
   * <p>Each instant is the beginning of the aggregation period defined by {@link
   * WeightedWasteOverviewInterface#getAggregationUnit()}, starting at the lower bound of {@link
   * WeightedWasteOverviewInterface#getPeriod()}.
   *
   * <p>Empty values are omitted.
   */
  ImmutableTable<Type, Instant, Integer> getFrequencies();

  /** The period of the overview. */
  Range<Instant> getPeriod();

  /** The aggregation unit that defines the aggregation period. */
  ChronoUnit getAggregationUnit();
}
