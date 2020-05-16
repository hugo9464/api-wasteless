package io.wastelesscorp.platform.atoms.user.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import org.bson.types.ObjectId;
import org.immutables.value.Value;

@Value.Immutable
public interface UserInterface {
  /** The user's ID. */
  @Value.Parameter(false)
  @Value.Default
  @JsonProperty("_id")
  default String getId() {
    return ObjectId.get().toHexString();
  }

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
}
