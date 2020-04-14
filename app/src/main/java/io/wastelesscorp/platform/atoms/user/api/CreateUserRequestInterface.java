package io.wastelesscorp.platform.atoms.user.api;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.immutables.value.Value;

/** Represents a request to create an user. */
@Value.Immutable
public interface CreateUserRequestInterface {
    /** The user email. */
    String getEmail();

    /** The encoded user password. */
    String getPassword();

    /** The user authorities. */
    ImmutableSet<Role> getRoles();

    default User toUser() {
        return User.of( // TODO should be some how an object id.
                UUID.randomUUID().toString(), getEmail(), getPassword(), getRoles());
    }
}
