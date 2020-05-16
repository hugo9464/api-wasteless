package io.wastelesscorp.platform.atoms.weightedwaste.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import org.bson.types.ObjectId;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteInterface {
  /** The ID of the weighted waste. */
  @Value.Parameter(false)
  @Value.Default
  @JsonProperty("_id")
  default String getId() {
    return ObjectId.get().toHexString();
  }

  /** The challenge ID associated to the weighted waste. */
  String getChallengeId();

  /** The user ID associated to the weighted waste. */
  String getUserId();

  /** The waste type. */
  Type getType();

  /** The weight of the waste. */
  Integer getWeight();

  /** The creation instant of the weighted waste. */
  Instant getCreatedAt();

  /** The waste type. */
  enum Type {
    GREEN,
    BLUE;
  }
}
