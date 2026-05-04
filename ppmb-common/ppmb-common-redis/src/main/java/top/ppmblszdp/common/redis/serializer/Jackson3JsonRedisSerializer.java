package top.ppmblszdp.common.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.ObjectMapper;

/**
 * Jackson 3 implementation of {@link RedisSerializer}.
 *
 * @param <T> the type to serialize/deserialize
 */
public class Jackson3JsonRedisSerializer<T> implements RedisSerializer<T> {

  private final ObjectMapper objectMapper;
  private final Class<T> clazz;

  public Jackson3JsonRedisSerializer(ObjectMapper objectMapper, Class<T> clazz) {
    this.objectMapper = objectMapper;
    this.clazz = clazz;
  }

  @Override
  public byte[] serialize(T t) throws SerializationException {
    if (t == null) {
      return new byte[0];
    }
    try {
      return objectMapper.writeValueAsBytes(t);
    } catch (Exception e) {
      throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
    }
  }

  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    try {
      return objectMapper.readValue(bytes, clazz);
    } catch (Exception e) {
      throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
    }
  }
}
