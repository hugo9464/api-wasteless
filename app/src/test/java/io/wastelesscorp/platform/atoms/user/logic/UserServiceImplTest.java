package io.wastelesscorp.platform.atoms.user.logic;

import static io.wastelesscorp.platform.atoms.user.api.Role.STANDARD_USER;

import com.google.common.collect.ImmutableSet;
import com.mongodb.BasicDBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.CreateUserRequest;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = UserServiceConfig.class)
class UserServiceImplTest {
  private static final CreateUserRequest CREATE_REQUEST =
      CreateUserRequest.of(
          "email", "password", "firstname", "lastname", ImmutableSet.of(STANDARD_USER));
  private static final User EXPECTED_USER =
      User.of("id", "email", "password", "firstname", "lastname", ImmutableSet.of(STANDARD_USER));

  @Autowired UserService service;
  @Autowired MongoCollection<User> collection;

  @BeforeEach
  public void setUp() {
    Mono.from(collection.deleteMany(new BasicDBObject())).block();
  }

  @Test
  void createAndFind() {
    StepVerifier.create(service.create(CREATE_REQUEST).then(service.findByEmail("email")))
        .expectNextMatches(actual -> IgnoreIdCompare(actual, EXPECTED_USER))
        .verifyComplete();
  }

  @Test
  void duplicatedCreation() {
    StepVerifier.create(service.create(CREATE_REQUEST).then(service.create(CREATE_REQUEST)))
        .verifyError(DuplicatedUserException.class);
  }

  private boolean IgnoreIdCompare(User actual, User expected) {
    return actual.withId(expected.getId()).equals(expected);
  }
}
