package io.wastelesscorp.platform.app.controller.weightedwaste;

import static io.wastelesscorp.platform.app.controller.weightedwaste.WeightedWasteController.UNIQUE_CHALLENGE_ID;
import static io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type.BLUE;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteOverview;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteService;
import io.wastelesscorp.platform.support.WebFluxConfig;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

@WebFluxTest
@Import({WeightedWasteController.class, WebFluxConfig.class})
@WithMockUser(
        username = "user_id",
        roles = {"standard_user"})
class WeightedWasteControllerTest {
    private static final String USER_ID = "user_id";
    private static final WeightedWaste WEIGHTED_WASTE =
            WeightedWaste.of(UNIQUE_CHALLENGE_ID, USER_ID, BLUE, 20, Instant.EPOCH);
    private static final WeightedWasteOverview OVERVIEW =
            WeightedWasteOverview.of(ImmutableTable.of(), Range.all(), DAYS);
    @MockBean private WeightedWasteService weightedWasteService;
    @Autowired private WebTestClient webTestClient;
    @Autowired private Clock clock;

    @Test
    void getWeightedWastes() {
        when(weightedWasteService.getWeightedWastes(UNIQUE_CHALLENGE_ID, ImmutableSet.of(USER_ID)))
                .thenReturn(Flux.just(WEIGHTED_WASTE));
        webTestClient
                .get()
                .uri("/api/app/weightedwaste/1/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(WeightedWaste.class)
                .hasSize(1)
                .contains(WEIGHTED_WASTE);
    }

    @Test
    void addWeightedWaste() {
        PublisherProbe<Void> insertProbe = PublisherProbe.empty();
        when(weightedWasteService.addWeightedWaste(
                        USER_ID,
                        WeightedWasteCreateRequest.of(
                                UNIQUE_CHALLENGE_ID, BLUE, 20, clock.instant())))
                .thenReturn(insertProbe.mono());
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/api/app/weightedwaste/1/")
                .body(
                        Mono.just(WeightedWasteCreateJsonRequest.of(BLUE, 20)),
                        WeightedWasteCreateJsonRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent();

        insertProbe.assertWasSubscribed();
    }

    @Test
    void getOverview() {
        when(weightedWasteService.getWeightedWasteOverview(
                        ImmutableSet.of(USER_ID), UNIQUE_CHALLENGE_ID, Range.all(), DAYS))
                .thenReturn(Mono.just(OVERVIEW));
        webTestClient
                .get()
                .uri("/api/app/weightedwaste/1/overview")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(WeightedWasteOverview.class)
                .hasSize(1)
                .contains(OVERVIEW);
    }

    @Configuration
    static class Config {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
        }
    }
}
