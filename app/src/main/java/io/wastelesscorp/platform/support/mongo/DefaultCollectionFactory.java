package io.wastelesscorp.platform.support.mongo;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.lang.reflect.Type;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

final class DefaultCollectionFactory implements CollectionFactory {
  private final MongoDatabase mongoDatabase;
  private final ObjectMapper codecRegistryObjectMapper;

  DefaultCollectionFactory(MongoDatabase mongoDatabase, ObjectMapper codecRegistryObjectMapper) {
    this.mongoDatabase = mongoDatabase;
    this.codecRegistryObjectMapper = codecRegistryObjectMapper;
  }

  public <T> MongoCollection<T> get(String collectionName, Class<T> clazz) {
    return mongoDatabase
        .withCodecRegistry(getCodecRegistry(clazz))
        .getCollection(collectionName, clazz);
  }

  private <T> CodecRegistry getCodecRegistry(Class<T> clazz) {
    return CodecRegistries.fromRegistries(
        getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(
            new JacksonGenericCodecProvider(
                codecRegistryObjectMapper,
                new TypeReference<T>() {
                  @Override
                  public Type getType() {
                    return clazz;
                  }
                })));
  }
}
