package io.wastelesscorp.platform.atoms.weightedwaste.api;

import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWasteInterface.Type;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteCreateRequestInterface {
  /** The challenge ID associated to the request. */
  String getChallengeId();

  /** The waste type. */
  Type getType();

  /** The weight of the waste. */
  Integer getWeight();

  /** The creation instant of the request. */
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
