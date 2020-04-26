package io.wastelesscorp.platform.app.security;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import io.wastelesscorp.platform.app.security.auth.basic.BasicAuthenticationSuccessHandler;
import io.wastelesscorp.platform.app.security.auth.basic.WastelessUserDetailService;
import io.wastelesscorp.platform.app.security.auth.bearer.BearerTokenReactiveAuthenticationManager;
import io.wastelesscorp.platform.app.security.auth.bearer.ServerHttpBearerAuthenticationConverter;
import io.wastelesscorp.platform.app.security.auth.jwt.JwtTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@Import({JwtTokenService.class, WastelessUserDetailService.class})
public class SecurityConfig {
  /**
   * For Spring Security webflux, a chain of filters will provide user authentication and
   * authorization, we add custom filters to enable JWT token approach.
   *
   * @param http An initial object to build common filter scenarios. Customized filters are added
   *     here.
   * @param userDetailService
   * @param jwtTokenService
   * @return SecurityWebFilterChain A filter chain for web exchanges that will provide security
   */
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http,
      ReactiveUserDetailsService userDetailService,
      JwtTokenService jwtTokenService) {
    // TODO can be simplified I think

    return http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .requestCache()
        .disable()
        .authorizeExchange()
        .pathMatchers(
            "/api/auth/1/signup", "/swagger/**", "/webjars/**", "/v3/**", "/swagger-ui.html")
        .permitAll()
        .and()
        .authorizeExchange()
        .pathMatchers("/api/auth/1/login")
        .authenticated()
        .and()
        .addFilterAt(
            basicAuthenticationFilter(userDetailService, jwtTokenService),
            SecurityWebFiltersOrder.HTTP_BASIC)
        .authorizeExchange()
        .pathMatchers("/api/app/**")
        .authenticated()
        .and()
        .addFilterAt(
            bearerAuthenticationFilter(jwtTokenService), SecurityWebFiltersOrder.AUTHENTICATION)
        .csrf()
        .disable()
        .build();
  }

  /**
   * Use the already implemented logic in AuthenticationWebFilter and set a custom SuccessHandler
   * that will return a JWT when a user is authenticated with user/password Create an
   * AuthenticationManager using the UserDetailsService defined above
   *
   * <p>TODO do not declare this one as bean to avoid duplicate registration
   *
   * @return AuthenticationWebFilter
   */
  public AuthenticationWebFilter basicAuthenticationFilter(
      ReactiveUserDetailsService service, JwtTokenService jwtTokenService) {
    AuthenticationWebFilter basicAuthenticationFilter =
        new AuthenticationWebFilter(
            new UserDetailsRepositoryReactiveAuthenticationManager(service));
    basicAuthenticationFilter.setAuthenticationSuccessHandler(
        new BasicAuthenticationSuccessHandler(jwtTokenService));
    basicAuthenticationFilter.setRequiresAuthenticationMatcher(
        ServerWebExchangeMatchers.pathMatchers("/api/auth/1/login"));
    return basicAuthenticationFilter;
  }

  /**
   * Use the already implemented logic by AuthenticationWebFilter and set a custom converter that
   * will handle requests containing a Bearer token inside the HTTP Authorization header. Set a
   * dummy authentication manager to this filter, it's not needed because the converter handles
   * this.
   *
   * @return bearerAuthenticationFilter that will authorize requests containing a JWT
   * @param jwtTokenService
   */
  private AuthenticationWebFilter bearerAuthenticationFilter(JwtTokenService jwtTokenService) {
    AuthenticationWebFilter bearerAuthenticationFilter =
        new AuthenticationWebFilter(new BearerTokenReactiveAuthenticationManager());
    bearerAuthenticationFilter.setServerAuthenticationConverter(
        new ServerHttpBearerAuthenticationConverter(jwtTokenService));
    bearerAuthenticationFilter.setRequiresAuthenticationMatcher(
        ServerWebExchangeMatchers.pathMatchers("/api/app/**"));
    return bearerAuthenticationFilter;
  }

  @Bean
  public PasswordEncoder delegatingPasswordEncoder() {
    return createDelegatingPasswordEncoder();
  }
}
