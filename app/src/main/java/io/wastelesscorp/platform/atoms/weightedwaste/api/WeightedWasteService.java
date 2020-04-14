package io.wastelesscorp.platform.atoms.weightedwaste.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeightedWasteService {
    Mono<Void> createWeightedWaste(String userId, WeightedWasteCreateRequest request);

    Flux<WeightedWaste> getWeightedWastes(String userId);
}
