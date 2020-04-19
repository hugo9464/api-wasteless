package io.wastelesscorp.platform.atoms.weightedwaste.logic;

import static io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type.BLUE;
import static io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type.GREEN;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverview;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import java.time.Clock;
import java.time.Instant;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = WeightedWasteServiceConfig.class)
class WeightedWasteServiceImplTest {
    private static final Clock CLOCK = Clock.fixed(Instant.EPOCH, systemDefault());
    private static final String CHALLENGE_ID = "challenge_id";
    private static final String USER_ID = "user_id";
    private static final WeightedWasteCreateRequest CREATE_REQUEST =
            WeightedWasteCreateRequest.of(CHALLENGE_ID, GREEN, 20, CLOCK.instant());
    private static final WeightedWaste EXPECTED_WEIGHTED_WASTE =
            WeightedWaste.of(CHALLENGE_ID, USER_ID, GREEN, 20, CLOCK.instant());

    @Autowired WeightedWasteService service;
    @Autowired MongoCollection<WeightedWaste> collection;

    @BeforeEach
    public void setUp() {
        Mono.from(collection.drop()).block();
    }

    @Test
    void createAndFind() {
        StepVerifier.create(
                        service.addWeightedWaste(USER_ID, CREATE_REQUEST)
                                .thenMany(
                                        service.getWeightedWastes(
                                                CHALLENGE_ID, ImmutableSet.of(USER_ID))))
                .expectNext(EXPECTED_WEIGHTED_WASTE)
                .verifyComplete();
    }

    @Test
    void overview() {
        Instant instant = CREATE_REQUEST.getCreatedAt();
        Range<Instant> period = Range.all();
        ChronoUnit aggregationUnit = DAYS;
        StepVerifier.create(
                        Flux.just(
                                        CREATE_REQUEST,
                                        CREATE_REQUEST.withType(BLUE),
                                        CREATE_REQUEST.withCreatedAt(instant.plus(1, HOURS)),
                                        CREATE_REQUEST.withCreatedAt(instant.plus(1, DAYS)))
                                .flatMap(request -> service.addWeightedWaste(USER_ID, request))
                                .then(
                                        service.getWeightedWasteOverview(
                                                ImmutableSet.of(USER_ID),
                                                CHALLENGE_ID,
                                                period,
                                                DAYS)))
                .expectNext(
                        WeightedWasteOverview.of(
                                ImmutableTable.<Type, Instant, Integer>builder()
                                        .put(GREEN, Instant.EPOCH, 40)
                                        .put(BLUE, Instant.EPOCH, 20)
                                        .put(GREEN, Instant.EPOCH.plus(1, DAYS), 20)
                                        .build(),
                                period,
                                aggregationUnit))
                .verifyComplete();
    }
}
