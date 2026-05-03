package top.ppmblszdp.common.redis.util;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@DisplayName("TwoLevelCacheMessageListener 单元测试")
class TwoLevelCacheMessageListenerTest {

  @Test
  @DisplayName("应能正确处理缓存失效消息")
  @SuppressWarnings("removal")
  void shouldHandleCacheInvalidationMessage() {
    // Arrange
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    ObjectMapper objectMapper = new ObjectMapper();
    // 必须激活默认类型，以支持 GenericJackson2JsonRedisSerializer 的多态序列化
    objectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    String cacheName = "userCache";
    String key = "user:1";
    TwoLevelCacheMessage cacheMessage = new TwoLevelCacheMessage(cacheName, key);

    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);
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
  @SuppressWarnings("removal")
  void shouldNotThrowExceptionWhenDeserializationFails() {
    // Arrange
    TwoLevelCacheManager cacheManager = mock(TwoLevelCacheManager.class);
    ObjectMapper objectMapper = new ObjectMapper();
    TwoLevelCacheMessageListener listener =
        new TwoLevelCacheMessageListener(cacheManager, objectMapper);

    Message message = mock(Message.class);
    when(message.getBody()).thenReturn("invalid".getBytes());

    // Act & Assert
    // 不应抛出异常，内部 catch 并记录日志
    listener.onMessage(message, null);

    verify(cacheManager, never()).clearLocal(anyString(), anyString());
  }
}
