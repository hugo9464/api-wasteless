package io.wastelesscorp.platform.support.mongo;

import com.mongodb.reactivestreams.client.MongoCollection;
import java.time.Clock;

/**
 * A {@link CollectionFactory} that append nano time to collection names.
 *
 * <p>For test purpose only.
 */
final class TestCollectionFactory implements CollectionFactory {
    private final DefaultCollectionFactory defaultCollectionFactory;
    private final Clock clock;

    TestCollectionFactory(DefaultCollectionFactory defaultCollectionFactory, Clock clock) {
        this.defaultCollectionFactory = defaultCollectionFactory;
        this.clock = clock;
    }

    public <T> MongoCollection<T> get(String collectionName, Class<T> clazz) {
        return defaultCollectionFactory.get(
                collectionName + "_" + clock.instant().getNano(), clazz);
    }
}
