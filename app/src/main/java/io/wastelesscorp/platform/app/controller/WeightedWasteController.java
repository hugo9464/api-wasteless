package io.wastelesscorp.platform.app.controller;

import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/app/weightedwaste/1/")
public class WeightedWasteController {
    private final WeightedWasteService weightedWasteService;

    public WeightedWasteController(WeightedWasteService weightedWasteService) {
        this.weightedWasteService = weightedWasteService;
    }

    @GetMapping
    public Flux<WeightedWaste> getWeightedWastes(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .flatMapMany(weightedWasteService::getWeightedWastes);
    }

    @PostMapping
    // TODO check 204 inferred by spring
    public Mono<Void> getWeightedWastes(
            Mono<Principal> principal, @RequestBody WeightedWasteCreateRequest request) {
        return principal
                .map(Principal::getName)
                .flatMap(userId -> weightedWasteService.createWeightedWaste(userId, request));
    }
}
