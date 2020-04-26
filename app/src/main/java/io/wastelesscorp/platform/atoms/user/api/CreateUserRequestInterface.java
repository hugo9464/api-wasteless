package io.wastelesscorp.platform.atoms.user.api;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.immutables.value.Value;

/** Represents a request to create an user. */
@Value.Immutable
public interface CreateUserRequestInterface {
  /** The user's email. */
  String getEmail();

  /** The user's last name. */
  String getFirstName();

  /** The user's last name. */
  String getLastName();

  /** The user's encoded password. */
  String getPassword();

  /** The user's authorities. */
  ImmutableSet<Role> getRoles();

  default User toUser() {
    return User.of( // TODO should be some how an object id.
        UUID.randomUUID().toString(),
        getEmail(),
        getFirstName(),
        getLastName(),
        getPassword(),
        getRoles());
  }
}
