package io.wastelesscorp.platform.atoms.user.logic.repository;

import static com.mongodb.client.model.Filters.eq;

import com.google.common.collect.ImmutableSet;
import com.mongodb.QueryBuilder;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.User;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;

public class UserRepository {
    private final MongoCollection<User> userMongoCollection;

    public UserRepository(MongoCollection<User> userMongoCollection) {
        this.userMongoCollection = userMongoCollection;
    }

    public Mono<User> findByEmail(String email) {
        return Mono.from(userMongoCollection.find(eq("email", email), User.class).first());
    }

    public Mono<Void> insert(User user) {
        return Mono.from(userMongoCollection.insertOne(user)).then();
    }
}
