package io.wastelesscorp.platform.support.mongo;

import static java.nio.ByteBuffer.wrap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.mongodb.MongoException;
import de.undercouch.bson4jackson.BsonGenerator;
import java.io.IOException;
import org.bson.BsonBinaryReader;
import org.bson.BsonWriter;
import org.bson.ByteBufNIO;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;
import org.bson.io.OutputBuffer;
import org.mongojack.MongoJsonMappingException;
import org.mongojack.internal.stream.DBEncoderBsonGenerator;
import org.mongojack.internal.stream.OutputBufferOutputStream;

final class JacksonEncoder<T> implements Encoder<T> {
    private final Class<T> clazz;
    private final ObjectMapper objectMapper;

    JacksonEncoder(Class<T> clazz, ObjectMapper objectMapper) {
        this.clazz = clazz;
        this.objectMapper = objectMapper;
    }

    private void writeObject(OutputBuffer buf, T object) throws IOException {
        OutputBufferOutputStream stream = new OutputBufferOutputStream(buf);
        try (BsonGenerator generator =
                new DBEncoderBsonGenerator(JsonGenerator.Feature.collectDefaults(), stream)) {
            objectMapper.writeValue(generator, object);
            // The generator buffers everything so that it can write the number of bytes to the
            // stream
        } catch (JsonMappingException e) {
            throw new MongoJsonMappingException(e);
        }
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        try (BasicOutputBuffer buffer = new BasicOutputBuffer()) {
            // XXX: There is a special case for date and time related entities, which are not
            // properly encoded. The encoder below writes a full document instead of just a value,
            // so the BSON that we send in the end is malformatted. Primitives follow a different
            // code path as they are not serialized via Jackson. Dates, Times, Instants and
            // Durations are neither though, so we have a special case for handling them.
            //if (DateSerializationHelper.trySerialize(writer, value)) {
              //  return;
            //}

            // Enums need some special attention, as they are neither primitive nor real objects.
            if (value.getClass().isEnum()) {
                writer.writeString(((Enum) value).name());
                return;
            }

            // Correctly serialise for @JsonValue-annotated types.
            if (objectMapper.getSerializerProviderInstance().findValueSerializer(value.getClass())
                    instanceof JsonValueSerializer) {
                writer.writeString(
                        objectMapper.readValue(
                                objectMapper.writeValueAsBytes(value), String.class));
                return;
            }

            writeObject(buffer, value);
            try (BsonBinaryReader reader =
                    new BsonBinaryReader(
                            new ByteBufferBsonInput(new ByteBufNIO(wrap(buffer.toByteArray()))))) {
                writer.pipe(reader);
            }
        } catch (IOException e) {
            throw new MongoException("Error writing object out", e);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }
}
