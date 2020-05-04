package io.wastelesscorp.platform.support.math;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
public interface SeriesSummaryInterface {
  /** The total result of the series. */
  int getTotal();

  /** The size of the series. */
  int getCount();

  /** The average result by element of the series. */
  @JsonSerialize
  @Value.Lazy
  default int getAverage() {
    return getCount() == 0 ? 0 : Math.round(getTotal() / getCount());
  }

  default SeriesSummary merge(SeriesSummaryInterface series) {
    return SeriesSummary.of(getTotal() + series.getTotal(), getCount() + series.getCount());
  }

  static SeriesSummary of(int total) {
    return SeriesSummary.of(total, 1);
  }
}
