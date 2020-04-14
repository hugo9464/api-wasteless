package io.wastelesscorp.platform.support.mongo;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.lang.reflect.Type;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MongoConfig.CollectionFactory.class)
public class MongoConfig {
    @Bean
    public MongoClient mongoClient(@Value("${mongodb.url}") String mongoUrl) {
        return MongoClients.create(mongoUrl);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        // TODO Randomize for test
        return mongoClient.getDatabase("wastelessdev");
    }

    @Qualifier("codecRegistryObjectMapper")
    @Bean
    ObjectMapper codecRegistryObjectMapper() {
        // TODO Find a way to handle mongo object id in a hided way
        return new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new GuavaModule())
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule());
    }

    public static final class CollectionFactory {
        private final MongoDatabase mongoDatabase;
        private final ObjectMapper codecRegistryObjectMapper;

        CollectionFactory(MongoDatabase mongoDatabase, ObjectMapper codecRegistryObjectMapper) {
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
}
