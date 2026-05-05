package top.ppmblszdp.common.redis.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.ObjectMapper;

@DisplayName("Jackson3JsonRedisSerializer 单元测试")
class Jackson3JsonRedisSerializerTest {

  private ObjectMapper objectMapper;
  private Jackson3JsonRedisSerializer<String> serializer;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    serializer = new Jackson3JsonRedisSerializer<>(objectMapper, String.class);
  }

  @Test
  @DisplayName("序列化 null 应返回空字节数组")
  void serializeNull() {
    assertThat(serializer.serialize(null)).isEmpty();
  }

  @Test
  @DisplayName("正常序列化对象")
  void serializeObject() {
    byte[] bytes = serializer.serialize("test");
    assertThat(bytes).isNotEmpty();
  }

  @Test
  @DisplayName("序列化异常应抛出 SerializationException")
  void serializeException() throws Exception {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    when(mockMapper.writeValueAsBytes(any())).thenThrow(new RuntimeException("mock error"));
    Jackson3JsonRedisSerializer<String> mockSerializer =
        new Jackson3JsonRedisSerializer<>(mockMapper, String.class);

    assertThatThrownBy(() -> mockSerializer.serialize("test"))
        .isInstanceOf(SerializationException.class)
        .hasMessageContaining("Could not write JSON");
  }

  @Test
  @DisplayName("反序列化 null 或空字节数组应返回 null")
  void deserializeNullOrEmpty() {
    assertThat(serializer.deserialize(null)).isNull();
    assertThat(serializer.deserialize(new byte[0])).isNull();
  }

  @Test
  @DisplayName("正常反序列化字节数组")
  void deserializeBytes() {
    byte[] bytes = serializer.serialize("test");
    String result = serializer.deserialize(bytes);
    assertThat(result).isEqualTo("test");
  }

  @Test
  @DisplayName("反序列化异常应抛出 SerializationException")
  void deserializeException() throws Exception {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    when(mockMapper.readValue(any(byte[].class), eq(String.class)))
        .thenThrow(new RuntimeException("mock error"));
    Jackson3JsonRedisSerializer<String> mockSerializer =
        new Jackson3JsonRedisSerializer<>(mockMapper, String.class);

    assertThatThrownBy(() -> mockSerializer.deserialize(new byte[] {1, 2, 3}))
        .isInstanceOf(SerializationException.class)
        .hasMessageContaining("Could not read JSON");
  }
}
