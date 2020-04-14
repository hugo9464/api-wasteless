package io.wastelesscorp.platform.atoms.weightedwaste.logic;

import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WeightedWasteServiceImpl implements WeightedWasteService {
    @Override
    public Mono<Void> createWeightedWaste(String userId, WeightedWasteCreateRequest request) {
        return Mono.error(new UnsupportedOperationException("Not yet implemented"));
    }

    @Override
    public Flux<WeightedWaste> getWeightedWastes(String userId) {
        return Flux.error(new UnsupportedOperationException("Not yet implemented"));
    }
}
