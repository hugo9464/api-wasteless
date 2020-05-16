package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeightedWasteService {
  /**
   * Stores a new weighted waste.
   *
   * @param userId The user ID of the weighted waste.
   * @param request The request to create.
   * @return A empty {@link Mono} in case of success.
   */
  Mono<Void> addWeightedWaste(String userId, WeightedWasteCreateRequest request);

  /**
   * Retrieves the {@link WeightedWasteOverview overview} of a challenge given the provided
   * criteria.
   *
   * @param challengeId The challenge ID for which we need to compute the overview.
   * @param userIds The user IDs for which we need to compute the overview; empty means all users.
   * @param period The period of the overview.
   * @param aggregationUnit The aggregation unit.
   * @return A {@link Mono} containing the {@link WeightedWasteOverview} matching the provided
   *     criteria.
   */
  Mono<WeightedWasteOverview> getWeightedWasteOverview(
      String challengeId,
      ImmutableSet<String> userIds,
      Range<Instant> period,
      ChronoUnit aggregationUnit);

  /**
   * Retrieves the {@link WeightedWaste}s of a challenge given the provided criteria.
   *
   * @param challengeId The challenge ID used to stored the {@link WeightedWaste}s.
   * @param userIds The user IDs used to stored the @link WeightedWaste}s; empty means all users.
   * @return A {@link Flux} containing the {@link WeightedWaste}s matching the provided criteria.
   */
  Flux<WeightedWaste> getWeightedWastes(String challengeId, ImmutableSet<String> userIds);

  /**
   * Retrieves the {@link WeightedWasteSummaryInterface}s of a challenge given the provided
   * criteria.
   *
   * @param challengeId The challenge ID used to stored the {@link WeightedWaste}s.
   * @param userIds The user IDs used to stored the @link WeightedWaste}s; empty means all users.
   * @return A {@link Mono} containing the {@link WeightedWasteSummaryInterface} matching the
   *     provided criteria.
   */
  Mono<WeightedWasteSummary> getWeightedWasteSummary(
      String challengeId, ImmutableSet<String> userIds);
}
