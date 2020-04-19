package io.wastelesscorp.platform.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableTable;
import java.io.IOException;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ImmutableTable.class, new TableDeserializer());
        simpleModule.addSerializer(new TableSerializer());
        return mapper.registerModule(new GuavaModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(simpleModule)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static class TableSerializer extends JsonSerializer<ImmutableTable<?, ?, ?>> {
        @Override
        public void serialize(
                final ImmutableTable<?, ?, ?> value,
                final JsonGenerator jgen,
                final SerializerProvider provider)
                throws IOException {
            jgen.writeObject(value.rowMap());
        }

        @Override
        public Class<ImmutableTable<?, ?, ?>> handledType() {
            return (Class) ImmutableTable.class;
        }
    } // end class TableSerializer

    public static class TableDeserializer extends JsonDeserializer<ImmutableTable<?, ?, ?>> {
        @Override
        public ImmutableTable<?, ?, ?> deserialize(
                final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final ImmutableTable.Builder<Object, Object, Object> tableBuilder =
                    ImmutableTable.builder();
            final Map<Object, Map<Object, Object>> rowMap = jp.readValueAs(Map.class);
            for (final Map.Entry<Object, Map<Object, Object>> rowEntry : rowMap.entrySet()) {
                final Object rowKey = rowEntry.getKey();
                for (final Map.Entry<Object, Object> cellEntry : rowEntry.getValue().entrySet()) {
                    final Object colKey = cellEntry.getKey();
                    final Object val = cellEntry.getValue();
                    tableBuilder.put(rowKey, colKey, val);
                }
            }
            return tableBuilder.build();
        }
    }

    @Bean
    Jackson2JsonEncoder jackson2JsonEncoder(ObjectMapper objectMapper) {
        return new Jackson2JsonEncoder(objectMapper);
    }

    @Bean
    Jackson2JsonDecoder jackson2JsonDecoder(ObjectMapper objectMapper) {
        return new Jackson2JsonDecoder(objectMapper);
    }

    @Bean
    WebFluxConfigurer webFluxConfigurer( Jackson2JsonDecoder jackson2JsonDecoder,Jackson2JsonEncoder jackson2JsonEncoder) {
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().jackson2JsonDecoder(jackson2JsonDecoder);
                configurer.defaultCodecs().jackson2JsonEncoder(jackson2JsonEncoder);
            }
        };
    }
}