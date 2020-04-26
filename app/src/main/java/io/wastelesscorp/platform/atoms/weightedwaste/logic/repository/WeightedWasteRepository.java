package io.wastelesscorp.platform.atoms.weightedwaste.logic.repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static io.wastelesscorp.platform.support.mongo.MongoUtils.inRange;
import static io.wastelesscorp.platform.support.mongo.MongoUtils.matchAllIfEmpty;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import java.time.Instant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WeightedWasteRepository {
  private static final String CHALLENGE_ID = "challengeId";
  private static final String CREATED_AT = "createdAt";
  private static final String USER_ID = "userId";
  private final MongoCollection<WeightedWaste> weightedWasteCollection;

  public WeightedWasteRepository(MongoCollection<WeightedWaste> weightedWasteCollection) {
    this.weightedWasteCollection = weightedWasteCollection;
  }

  public Mono<Void> insert(WeightedWaste document) {
    return Mono.from(weightedWasteCollection.insertOne(document)).then();
  }

  public Flux<WeightedWaste> findAll(
      String challengeId, ImmutableSet<String> userIds, Range<Instant> period) {
    return Flux.from(
        weightedWasteCollection.find(
            and(
                eq(CHALLENGE_ID, challengeId),
                inRange(CREATED_AT, period),
                matchAllIfEmpty(USER_ID, userIds))));
  }

  public Flux<WeightedWaste> findWeightedWastes(String challengeId, ImmutableSet<String> userIds) {
    return Flux.from(
        weightedWasteCollection.find(
            and(eq(CHALLENGE_ID, challengeId), matchAllIfEmpty(USER_ID, userIds))));
  }
}
