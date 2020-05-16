package io.wastelesscorp.platform.app.controller.weightedwaste;

import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteCreateRequest;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
interface WeightedWasteCreateJsonRequestInterface {
  Type getType();

  Integer getWeight();

  default WeightedWasteCreateRequest toDto(String challengeId, Instant now) {
    return WeightedWasteCreateRequest.of(challengeId, getType(), getWeight(), now);
  }
}
