package io.wastelesscorp.platform.app.security.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import io.wastelesscorp.platform.app.security.auth.jwt.JwtTokenService;
import io.wastelesscorp.platform.atoms.user.api.Role;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.api.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
  private static final String EMAIL = "email@email.com";
  private static final String PASSWORD = "password";
  private static final String USER_ID = "id";
  private static final String FIRSTNAME = "firstname";
  private static final String LASTNAME = "lastname";
  @MockBean private UserService userService;

  @Autowired private JwtTokenService jwtTokenService;
  @Autowired private WebTestClient webClient;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  public void messageWhenNotAuthenticated() {
    this.webClient.get().uri("/").exchange().expectStatus().isUnauthorized();
  }

  @Test
  public void testSignupScenario() {
    mockUserService();
    PublisherProbe<Void> createProbe = PublisherProbe.empty();
    when(userService.create(any())).thenReturn(createProbe.mono());
    this.webClient
        .post()
        .uri("/api/auth/1/signup")
        .body(
            Mono.just(SignupRequestJson.of(EMAIL, FIRSTNAME, LASTNAME, PASSWORD)),
            SignupRequestJson.class)
        .exchange()
        .expectStatus()
        .isOk();
    this.webClient
        .get()
        .uri("/api/auth/1/login")
        .headers(c -> c.setBasicAuth(EMAIL, PASSWORD))
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testBasicLogin() {
    mockUserService();
    this.webClient
        .get()
        .uri("/api/auth/1/login")
        .headers(c -> c.setBasicAuth(EMAIL, PASSWORD))
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .exists(HttpHeaders.AUTHORIZATION);
    expectUnauthorizedRequest(
        this.webClient
            .get()
            .uri("/api/auth/1/login")
            .headers(c -> c.setBasicAuth("random", PASSWORD))
            .exchange());
    expectUnauthorizedRequest(
        webClient
            .get()
            .uri("/api/auth/1/login")
            .headers(c -> c.setBasicAuth(EMAIL, "random"))
            .exchange());
  }

  @Test
  public void testJwtLogin() {
    String token =
        jwtTokenService.generateToken(
            USER_ID,
            null,
            ImmutableSet.of(new SimpleGrantedAuthority(Role.STANDARD_USER.getName())));
    webClient
        .get()
        .uri("/api/app/whatever")
        .headers(c -> c.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .doesNotExist(HttpHeaders.AUTHORIZATION);
    expectUnauthorizedRequest(this.webClient.get().uri("/api/app/1/login").exchange());
    expectUnauthorizedRequest(
        this.webClient
            .get()
            .uri("/api/app/1/login")
            .headers(c -> c.setBearerAuth("random"))
            .exchange());
  }

  private void mockUserService() {
    User validUser =
        User.of(
            EMAIL,
            FIRSTNAME,
            LASTNAME,
            passwordEncoder.encode(PASSWORD),
            ImmutableSet.of(Role.STANDARD_USER));
    when(userService.findByEmail(any()))
        .thenAnswer(
            inv -> Mono.just(validUser).filter(u -> u.getEmail().equals(inv.getArgument(0))));
  }

  private static void expectUnauthorizedRequest(WebTestClient.ResponseSpec exchange) {
    exchange.expectStatus().isUnauthorized().expectHeader().doesNotExist(HttpHeaders.AUTHORIZATION);
  }
}
