package io.wastelesscorp.platform.support.mongo;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.io.BasicOutputBuffer;
import org.mongojack.internal.stream.DBDecoderBsonParser;
import org.mongojack.internal.stream.JacksonDBObject;

final class JacksonDecoder<T> implements Decoder<T> {
    private final TypeReference<?> typeReference;
    private final ObjectMapper objectMapper;

    JacksonDecoder(Class<T> clazz, ObjectMapper objectMapper) {
        this(new TypeReference<>() {
                    @Override
                    public Type getType() {
                        return clazz;
                    }
                },
                objectMapper);
    }

    JacksonDecoder(TypeReference<?> typeReference, ObjectMapper objectMapper) {
        this.typeReference = typeReference;
        this.objectMapper = objectMapper;
    }

    private T decode(byte[] b) {
        try {
            return decode(new ByteArrayInputStream(b));
        } catch (IOException e) {
            // Not possible
            throw new UncheckedIOException(
                    "IOException encountered while reading from a byte array input stream", e);
        }
    }

    private T decode(InputStream in) throws IOException {
        JacksonDBObject<T> decoded = new JacksonDBObject<T>();
        try (DBDecoderBsonParser parser =
                     new DBDecoderBsonParser(
                             new IOContext(new BufferRecycler(), in, false),
                             0,
                             in,
                             decoded,
                             null,
                             objectMapper)) {
            return objectMapper.reader().forType(typeReference).readValue(parser);
        }
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        try (BasicOutputBuffer bob = new BasicOutputBuffer();
             BsonBinaryWriter binaryWriter = new BsonBinaryWriter(bob)) {
            binaryWriter.pipe(reader);
            return decode(bob.getInternalBuffer());
        }
    }
}
