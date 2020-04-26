package io.wastelesscorp.platform.atoms.user.logic;

import io.wastelesscorp.platform.atoms.user.api.CreateUserRequest;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.api.UserService;
import io.wastelesscorp.platform.atoms.user.logic.repository.UserRepository;
import reactor.core.publisher.Mono;

public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Mono<Void> create(CreateUserRequest request) {
    return Mono.fromSupplier(request::toUser).flatMap(userRepository::insert);
  }

  @Override
  public Mono<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
