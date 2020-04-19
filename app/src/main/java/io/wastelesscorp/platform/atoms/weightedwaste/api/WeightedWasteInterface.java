package io.wastelesscorp.platform.atoms.weightedwaste.api;

import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteInterface {
    String getChallengeId();

    String getUserId();

    Type getType();

    Integer getWeight();

    Instant getCreatedAt();

    enum Type {
        GREEN,
        BLUE;
    }
}
