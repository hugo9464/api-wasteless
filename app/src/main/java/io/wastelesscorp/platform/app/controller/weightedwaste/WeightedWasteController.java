package io.wastelesscorp.platform.app.controller.weightedwaste;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverview;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import java.awt.*;
import java.security.Principal;
import java.time.Clock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Weighted Waste", description = "Handles weighted waste related ressources.")
@RestController
@RequestMapping("/api/app/weightedwaste/1/")
public class WeightedWasteController {
    @VisibleForTesting static final String UNIQUE_CHALLENGE_ID = "unique_challenge_id";
    private final WeightedWasteService weightedWasteService;
    private final Clock clock;

    public WeightedWasteController(WeightedWasteService weightedWasteService, Clock clock) {
        this.weightedWasteService = weightedWasteService;
        this.clock = clock;
    }

    @GetMapping
    public Flux<WeightedWaste> getWeightedWastes(
            @AuthenticationPrincipal Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(ImmutableSet::of)
                .flatMapMany(
                        userIds ->
                                weightedWasteService.getWeightedWastes(
                                        UNIQUE_CHALLENGE_ID, userIds));
    }

    @PostMapping
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> addWeightedWaste(
            @AuthenticationPrincipal Mono<Principal> principal,
            @RequestBody WeightedWasteCreateJsonRequest request) {
        return principal
                .map(Principal::getName)
                .flatMap(
                        userId ->
                                weightedWasteService.addWeightedWaste(
                                        userId,
                                        request.toDto(UNIQUE_CHALLENGE_ID, clock.instant())));
    }

    @GetMapping("/overview")
    public Mono<WeightedWasteOverview> getOverview(
            @AuthenticationPrincipal Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(ImmutableSet::of)
                .flatMap(
                        userIds ->
                                weightedWasteService.getWeightedWasteOverview(
                                        userIds, UNIQUE_CHALLENGE_ID, Range.all(), DAYS));
    }
}
