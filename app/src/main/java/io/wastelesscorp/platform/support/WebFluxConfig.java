package io.wastelesscorp.platform.support;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.CREATOR;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.io.IOException;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFlux
public class WebFluxConfig {
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
                .setVisibility(ALL, NONE)
                .setVisibility(CREATOR, ANY)
                .setVisibility(FIELD, ANY)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static class TableSerializer extends JsonSerializer<ImmutableTable> {
        @Override
        public void serialize(
                final ImmutableTable value,
                final JsonGenerator jgen,
                final SerializerProvider provider)
                throws IOException {
            jgen.writeObject(value.rowMap());
        }

        @Override
        public Class<ImmutableTable> handledType() {
            return ImmutableTable.class;
        }
    } // end class TableSerializer

    public static class TableDeserializer extends JsonDeserializer<ImmutableTable<?, ?, ?>> {
        @Override
        public ImmutableTable<?, ?, ?> deserialize(
                final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final ImmutableTable.Builder<Object, Object, Object> tableBuilder =
                    ImmutableTable.builder();
            final Map<?, Map<?, ?>> rowMap =
                    jp.readValueAs(new TypeReference<Map<?, Map<?, ?>>>() {});
            for (final Map.Entry<?, Map<?, ?>> rowEntry : rowMap.entrySet()) {
                final Object rowKey = rowEntry.getKey();
                for (final Map.Entry<?, ?> cellEntry : rowEntry.getValue().entrySet()) {
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
    WebFluxConfigurer webFluxConfigurer(
            Jackson2JsonDecoder jackson2JsonDecoder, Jackson2JsonEncoder jackson2JsonEncoder) {
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().enableLoggingRequestDetails(true);
                configurer.defaultCodecs().jackson2JsonDecoder(jackson2JsonDecoder);
                configurer.defaultCodecs().jackson2JsonEncoder(jackson2JsonEncoder);
            }
        };
    }

    @Bean
    public OpenAPI wastelessOpenAPI() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .in(HEADER)
                                                .bearerFormat("JWT")))
                .security(ImmutableList.of(new SecurityRequirement().addList("bearerAuth")));
    }
}
