package io.wastelesscorp.platform.atoms.user.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value.Immutable
public interface UserInterface {
    @JsonProperty("_id")
    String getId();

    String getEmail();

    String getPassword();

    ImmutableSet<Role> getRoles();
}
