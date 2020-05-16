package io.wastelesscorp.platform.atoms.user.logic.repository;

import static com.mongodb.client.model.Filters.eq;
import static io.wastelesscorp.platform.support.mongo.MongoUtils.UNIQUE_INDEX_OPTIONS;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.logic.DuplicatedUserException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Mono;

public class UserRepository implements ApplicationListener<ApplicationReadyEvent> {
  private static final String EMAIL = "email";
  private final MongoCollection<User> userCollection;

  public UserRepository(MongoCollection<User> userMongoCollection) {
    this.userCollection = userMongoCollection;
  }

  public Mono<User> findByEmail(String email) {
    return Mono.from(userCollection.find(eq("email", email), User.class).first());
  }

  public Mono<Void> insert(User document) {
    return Mono.from(userCollection.insertOne(document))
        .onErrorResume(
            MongoWriteException.class,
            e -> Mono.error(new DuplicatedUserException(document.getEmail())))
        .then();
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    Mono.from(userCollection.createIndex(Indexes.ascending(EMAIL), UNIQUE_INDEX_OPTIONS)).block();
  }
}
