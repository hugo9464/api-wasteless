package io.wastelesscorp.platform.atoms.weightedwaste.api;

import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteCreateRequestInterface {
  String getChallengeId();

  Type getType();

  Integer getWeight();

  Instant getCreatedAt();

  static WeightedWaste toWeightedWaste(String userId, WeightedWasteCreateRequest request) {
    return WeightedWaste.of(
        request.getChallengeId(),
        userId,
        request.getType(),
        request.getWeight(),
        request.getCreatedAt());
  }
}
