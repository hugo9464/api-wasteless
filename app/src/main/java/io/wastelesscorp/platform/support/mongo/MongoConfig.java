package io.wastelesscorp.platform.support.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(DefaultCollectionFactory.class)
public class MongoConfig {
    @Bean
    public MongoClient mongoClient(@Value("${mongodb.url}") String mongoUrl) {
        return MongoClients.create(mongoUrl);
    }

    @Primary
    @Profile("test")
    @Bean
    CollectionFactory testCollectionFactory(DefaultCollectionFactory defaultFactory) {
        return new TestCollectionFactory(defaultFactory, clock());
    }

    @Bean
    public MongoDatabase mongoDatabase(
            MongoClient mongoClient, @Value("${mongodb.database}") String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }

    @Bean
    @Qualifier("codecRegistryObjectMapper")
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

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
