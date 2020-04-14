package io.wastelesscorp.platform.support.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public final class JacksonCodecProvider implements CodecProvider {
    private final ObjectMapper objectMapper;

    public JacksonCodecProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return new JacksonCodec<>(
                new JacksonEncoder<>(clazz, objectMapper),
                new JacksonDecoder<>(clazz, objectMapper));
    }
}
