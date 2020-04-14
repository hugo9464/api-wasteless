package io.wastelesscorp.platform.app.security.controller;

import static io.wastelesscorp.platform.atoms.user.api.Role.STANDARD_USER;

import com.google.common.collect.ImmutableSet;
import io.wastelesscorp.platform.atoms.user.api.CreateUserRequest;
import org.immutables.value.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Represents a request to create an account */
@Value.Immutable
public interface SignupRequestJsonInterface {
    /** The user email */
    String getEmail();
    /** The user password */
    String getPassword();

    default CreateUserRequest toCreateUserRequest(PasswordEncoder passwordEncoder) {
        return CreateUserRequest.of(
                getEmail(), passwordEncoder.encode(getPassword()), ImmutableSet.of(STANDARD_USER));
    }
    // TODO Add validation
}
