package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeightedWasteService {
  Mono<Void> addWeightedWaste(String userId, WeightedWasteCreateRequest request);

  Mono<WeightedWasteOverview> getWeightedWasteOverview(
      ImmutableSet<String> userId,
      String challengeId,
      Range<Instant> period,
      ChronoUnit aggregationUnit);

  Flux<WeightedWaste> getWeightedWastes(String challengeId, ImmutableSet<String> userIds);
}
