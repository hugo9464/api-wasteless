package io.wastelesscorp.platform.atoms.weightedwaste.logic;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableTable.toImmutableTable;
import static java.util.stream.Collectors.collectingAndThen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequestInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverview;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteSummary;
import io.wastelesscorp.platform.atoms.weightedwaste.logic.repository.WeightedWasteRepository;
import io.wastelesscorp.platform.support.math.SeriesSummaryInterface;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WeightedWasteServiceImpl implements WeightedWasteService {
  public final WeightedWasteRepository repository;

  public WeightedWasteServiceImpl(WeightedWasteRepository repository) {
    this.repository = repository;
  }

  @Override
  public Mono<Void> addWeightedWaste(String userId, WeightedWasteCreateRequest request) {
    return Mono.fromSupplier(
            () -> WeightedWasteCreateRequestInterface.toWeightedWaste(userId, request))
        .flatMap(repository::insert);
  }

  @Override
  public Mono<WeightedWasteOverview> getWeightedWasteOverview(
      String challengeId,
      ImmutableSet<String> userIds,
      Range<Instant> period,
      ChronoUnit aggregationUnit) {
    return repository
        .findAll(challengeId, userIds, period)
        .collect(
            collectingAndThen(
                toImmutableTable(
                    WeightedWaste::getType,
                    ww -> ww.getCreatedAt().truncatedTo(aggregationUnit),
                    WeightedWasteInterface::getWeight,
                    Integer::sum),
                frequencies -> WeightedWasteOverview.of(frequencies, period, aggregationUnit)));
  }

  @Override
  public Flux<WeightedWaste> getWeightedWastes(String challengeId, ImmutableSet<String> userIds) {
    return repository.findWeightedWastes(challengeId, userIds);
  }

  @Override
  public Mono<WeightedWasteSummary> getWeightedWasteSummary(
      String challengeId, ImmutableSet<String> userIds) {
    return repository
        .findWeightedWastes(challengeId, userIds)
        .collect(
            collectingAndThen(
                toImmutableMap(
                    WeightedWaste::getType,
                    ww -> SeriesSummaryInterface.of(ww.getWeight()),
                    SeriesSummaryInterface::merge),
                WeightedWasteSummary::of));
  }
}
