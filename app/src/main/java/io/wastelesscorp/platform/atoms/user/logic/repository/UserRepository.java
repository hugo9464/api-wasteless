package io.wastelesscorp.platform.atoms.user.logic.repository;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.User;
import reactor.core.publisher.Mono;

public class UserRepository {
    private final MongoCollection<User> userCollection;

    public UserRepository(MongoCollection<User> userMongoCollection) {
        this.userCollection = userMongoCollection;
    }

    public Mono<User> findByEmail(String email) {
        return Mono.from(userCollection.find(eq("email", email), User.class).first());
    }

    public Mono<Void> insert(User document) {
        return Mono.from(userCollection.insertOne(document)).then();
    }
}
