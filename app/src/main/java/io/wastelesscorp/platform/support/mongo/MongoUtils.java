package io.wastelesscorp.platform.support.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lte;

import com.google.common.collect.Range;
import com.mongodb.BasicDBObject;
import java.util.Collection;
import org.bson.conversions.Bson;

public final class MongoUtils {

  private static final BasicDBObject NO_CRITERION = new BasicDBObject();

  private MongoUtils() {}

  public static Bson matchAllIfEmpty(String field, Collection<String> in) {
    if (in.isEmpty()) {
      return NO_CRITERION;
    }
    if (in.size() == 1) {
      return eq(field, in.iterator().next());
    }
    return in(field, in);
  }

  public static Bson inRange(String field, Range<?> range) {
    if (!range.hasLowerBound() && !range.hasUpperBound()) {
      return NO_CRITERION;
    }
    if (range.hasLowerBound() && range.hasUpperBound()) {
      return and(gte(field, range.lowerEndpoint()), lte(field, range.upperEndpoint()));
    }

    if (!range.hasLowerBound()) {
      return and(gte(field, range.lowerEndpoint()));
    }
    return lte(field, range.upperEndpoint());
  }
}
