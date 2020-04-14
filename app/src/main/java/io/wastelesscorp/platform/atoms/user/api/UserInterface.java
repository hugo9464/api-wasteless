package io.wastelesscorp.platform.atoms.user.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value.Immutable
public interface UserInterface {
    /** The user's ID. */
    @JsonProperty("_id")
    String getId();

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
