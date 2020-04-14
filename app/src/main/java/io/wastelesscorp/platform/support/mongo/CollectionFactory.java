package io.wastelesscorp.platform.support.mongo;

import com.mongodb.reactivestreams.client.MongoCollection;

public interface CollectionFactory {
    /**
     * Permits to retrieve a {@link MongoCollection} given the collection name and its document
     * type.
     *
     * @param collectionName The mongo collection name.
     * @param clazz The document type.
     */
    <T> MongoCollection<T> get(String collectionName, Class<T> clazz);
}
