package io.wastelesscorp.platform.atoms.user.logic;

import static com.mongodb.client.model.Filters.and;
import static io.wastelesscorp.platform.atoms.user.api.Role.STANDARD_USER;

import com.google.common.collect.ImmutableSet;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.CreateUserRequest;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = UserServiceConfig.class)
class UserServiceImplTest {
    private static final CreateUserRequest CREATE_REQUEST =
            CreateUserRequest.of(
                    "email", "password", "firstname", "lastname", ImmutableSet.of(STANDARD_USER));
    private static final User EXPECTED_USER =
            User.of(
                    "id",
                    "email",
                    "password",
                    "firstname",
                    "lastname",
                    ImmutableSet.of(STANDARD_USER));

    @Autowired UserService service;
    @Autowired MongoCollection<User> userMongoCollection;

    @BeforeEach
    public void setUp() {
        Mono.from(userMongoCollection.drop()).block();
    }

    @Test
    void createAndFind() {
        StepVerifier.create(service.create(CREATE_REQUEST).then(service.findByEmail("email")))
                .expectNextMatches(actual -> IgnoreIdCompare(actual, EXPECTED_USER))
                .verifyComplete();
    }

    @Test
    @Disabled("Needs to implement the feature. Use mongo unique index on email.")
    void duplicatedCreation() {
        StepVerifier.create(service.create(CREATE_REQUEST).then(service.create(CREATE_REQUEST)))
                .verifyError();
    }

    private boolean IgnoreIdCompare(User actual, User expected) {
        return actual.withId(expected.getId()).equals(expected);
    }
}
