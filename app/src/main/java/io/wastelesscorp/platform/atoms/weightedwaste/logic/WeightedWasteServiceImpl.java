package io.wastelesscorp.platform.atoms.weightedwaste.logic;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.ImmutableTable.toImmutableTable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequestInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverview;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverviewInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import io.wastelesscorp.platform.atoms.weightedwaste.logic.repository.WeightedWasteRepository;
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
            ImmutableSet<String> userId,
            String challengeId,
            Range<Instant> period,
            ChronoUnit aggregationUnit) {
        return repository
                .findAll(challengeId, userId, period)
                .collect(toImmutableSet())
                .map(weightedWastes -> toFrequencies(weightedWastes, aggregationUnit))
                .map(
                        frequencies ->
                                WeightedWasteOverviewInterface.of(
                                        frequencies, period, aggregationUnit));
    }

    @Override
    public Flux<WeightedWaste> getWeightedWastes(String challengeId, ImmutableSet<String> userIds) {
        return repository.findWeightedWastes(challengeId, userIds);
    }

    private static ImmutableTable<Type, Instant, Integer> toFrequencies(
            ImmutableSet<WeightedWaste> weightedWastes, ChronoUnit aggregationUnit) {
        return weightedWastes.stream()
                .collect(
                        toImmutableTable(
                                WeightedWaste::getType,
                                weightedWaste ->
                                        weightedWaste.getCreatedAt().truncatedTo(aggregationUnit),
                                WeightedWasteInterface::getWeight,
                                Integer::sum));
    }
}
