package io.wastelesscorp.platform.atoms.user.logic;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.user.api.User;
import io.wastelesscorp.platform.atoms.user.logic.repository.UserRepository;
import io.wastelesscorp.platform.support.mongo.MongoConfig;
import io.wastelesscorp.platform.support.mongo.MongoConfig.CollectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({UserServiceImpl.class, UserRepository.class, MongoConfig.class})
public class UserServiceConfig {
    @Bean
    MongoCollection<User> mongoCollection(CollectionFactory collectionFactory) {
        return collectionFactory.get("users", User.class);
    }
}
