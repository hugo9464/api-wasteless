package io.wastelesscorp.platform.atoms.weightedwaste.logic;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.wastelesscorp.platform.atoms.weightedwaste.api.WeightedWaste;
import io.wastelesscorp.platform.atoms.weightedwaste.logic.repository.WeightedWasteRepository;
import io.wastelesscorp.platform.support.mongo.CollectionFactory;
import io.wastelesscorp.platform.support.mongo.MongoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({MongoConfig.class, WeightedWasteRepository.class, WeightedWasteServiceImpl.class})
public class WeightedWasteServiceConfig {
  @Bean
  MongoCollection<WeightedWaste> weightedWasteCollection(CollectionFactory collectionFactory) {
    return collectionFactory.get("weighted_wastes", WeightedWaste.class);
  }
}
