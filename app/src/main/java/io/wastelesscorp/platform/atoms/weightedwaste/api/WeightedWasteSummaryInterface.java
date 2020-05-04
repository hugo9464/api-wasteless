package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.google.common.collect.ImmutableMap;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import io.wastelesscorp.platform.support.math.SeriesSummary;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteSummaryInterface {
  /**
   * The {@link SeriesSummary} that describe the total weighted waste by {@link WeightedWaste.Type
   * waste types}.
   */
  ImmutableMap<Type, SeriesSummary> getSummary();
}
