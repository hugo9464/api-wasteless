package io.wastelesscorp.platform.atoms.user.api;

import reactor.core.publisher.Mono;

/** The service responsible to manage users. */
public interface UserService {
    /**
     * Stores a user.
     *
     * @param request The request representing the user to create.
     * @return A Mono indicating the success of the operation.
     */
    Mono<Void> create(CreateUserRequest request);

    /**
     * Retrieves a user by the given email.
     *
     * @param email The email criteria.
     * @return A Mono containing the user; empty if no user exists.
     */
    Mono<User> findByEmail(String email);
}
