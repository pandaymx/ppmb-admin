package top.ppmblszdp.common.redis.util;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.Message;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import top.ppmblszdp.common.redis.serializer.Jackson3JsonRedisSerializer;

@DisplayName("TwoLevelCacheMessageListener 单元测试")
class TwoLevelCacheMessageListenerTest {

  @Test
  @DisplayName("应能正确处理缓存失效消息")
  void shouldHandleCacheInvalidationMessage() {
    // Arrange
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    // 必须激活默认类型，以支持 GenericJacksonJsonRedisSerializer 的多态序列化
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();
    ObjectMapper objectMapper =
        JsonMapper.builder()
            .activateDefaultTyping(ptv, tools.jackson.databind.DefaultTyping.NON_FINAL)
            .build();

    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    String cacheName = "userCache";
    String key = "user:1";
    TwoLevelCacheMessage cacheMessage = new TwoLevelCacheMessage(cacheName, key);

    Jackson3JsonRedisSerializer<Object> serializer =
        new Jackson3JsonRedisSerializer<>(objectMapper, Object.class);
    byte[] body = serializer.serialize(cacheMessage);

    Message message = mock(Message.class);
    when(message.getBody()).thenReturn(body);

    // Act
    listener.onMessage(message, null);

    // Assert
    verify(cacheManager).clearLocal(cacheName, key);
  }

  @Test
  @DisplayName("反序列化失败时不应抛出异常")
  void shouldNotThrowExceptionWhenDeserializationFails() {
    // Arrange
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    ObjectMapper objectMapper = JsonMapper.builder().build();

    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    Message message = mock(Message.class);
    when(message.getBody()).thenReturn("invalid".getBytes());

    // Act & Assert
    // 不应抛出异常，内部 catch 并记录日志
    listener.onMessage(message, null);

    verify(cacheManager, never()).clearLocal(anyString(), anyString());
  }

  @Test
  @DisplayName("消息内容不是 TwoLevelCacheMessage 时不应处理")
  void shouldNotHandleWhenNotCacheMessage() {
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();
    ObjectMapper objectMapper =
        JsonMapper.builder()
            .activateDefaultTyping(ptv, tools.jackson.databind.DefaultTyping.NON_FINAL)
            .build();

    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    Jackson3JsonRedisSerializer<Object> serializer =
        new Jackson3JsonRedisSerializer<>(objectMapper, Object.class);
    byte[] body = serializer.serialize("some other string");

    Message message = mock(Message.class);
    when(message.getBody()).thenReturn(body);

    listener.onMessage(message, null);

    verify(cacheManager, never()).clearLocal(anyString(), anyString());
  }

  @Test
  @DisplayName("应能重用已初始化的序列化器")
  void shouldReuseSerializer() {
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();
    ObjectMapper objectMapper =
        JsonMapper.builder()
            .activateDefaultTyping(ptv, tools.jackson.databind.DefaultTyping.NON_FINAL)
            .build();

    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    TwoLevelCacheMessage cacheMessage = new TwoLevelCacheMessage("name", "key");
    Jackson3JsonRedisSerializer<Object> serializer =
        new Jackson3JsonRedisSerializer<>(objectMapper, Object.class);
    byte[] body = serializer.serialize(cacheMessage);

    Message message = mock(Message.class);
    when(message.getBody()).thenReturn(body);

    // 第一次调用，初始化 serializer
    listener.onMessage(message, null);
    // 第二次调用，重用 serializer
    listener.onMessage(message, null);

    verify(cacheManager, times(2)).clearLocal("name", "key");
  }
}
