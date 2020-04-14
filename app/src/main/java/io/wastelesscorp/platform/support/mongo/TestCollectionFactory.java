package io.wastelesscorp.platform.support.mongo;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.lang.reflect.Type;
import java.time.Clock;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

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
                collectionName + "-" + clock.instant().getNano(), clazz);
    }
}
