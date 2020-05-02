package io.wastelesscorp.platform.atoms.weightedwaste.api;

import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
public interface WeightedWasteInterface {
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
