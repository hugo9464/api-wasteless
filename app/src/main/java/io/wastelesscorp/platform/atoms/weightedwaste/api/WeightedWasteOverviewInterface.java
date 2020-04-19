package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteOverviewInterface {
    ImmutableTable<Type, Instant, Integer> getFrequencies();

    Range<Instant> getPeriod();

    ChronoUnit getAggregationUnit();

    static WeightedWasteOverview of(
            ImmutableTable<Type, Instant, Integer> frequencies,
            Range<Instant> period,
            ChronoUnit aggregationUnit) {
        return WeightedWasteOverview.of(frequencies, period, aggregationUnit);
    }
}
