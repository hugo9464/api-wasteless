package io.wastelesscorp.platform.app.security.auth.basic;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import io.wastelesscorp.platform.atoms.user.api.Role;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.api.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public class WastelessUserDetailService implements ReactiveUserDetailsService {
  private final UserService userService;

  public WastelessUserDetailService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Mono<UserDetails> findByUsername(String email) {
    return userService.findByEmail(email).map(WastelessUserDetailService::buildUserDetails);
  }

  private static UserDetails buildUserDetails(User user) {
    return new org.springframework.security.core.userdetails.User(
        user.getId(),
        user.getPassword(),
        user.getRoles().stream()
            .map(Role::getName)
            .map(SimpleGrantedAuthority::new)
            .collect(toImmutableSet()));
  }
}
