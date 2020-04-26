package io.wastelesscorp.platform.support.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Nullable;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class JacksonGenericCodecProvider implements CodecProvider {
  private final ObjectMapper objectMapper;
  private final TypeReference<?> typeReference;
  private final Class<?> rawClass;

  public JacksonGenericCodecProvider(ObjectMapper objectMapper, TypeReference<?> typeReference) {
    this.objectMapper = objectMapper;
    this.typeReference = typeReference;
    this.rawClass = objectMapper.getTypeFactory().constructType(typeReference).getRawClass();
  }

  @Nullable
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
    if (clazz.equals(rawClass)) {
      return new JacksonCodec<>(
          new JacksonEncoder<>(clazz, objectMapper),
          new JacksonDecoder<>(typeReference, objectMapper));
    }

    return null;
  }
}
