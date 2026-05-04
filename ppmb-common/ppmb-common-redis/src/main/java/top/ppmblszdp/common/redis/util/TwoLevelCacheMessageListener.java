package top.ppmblszdp.common.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "ppmb.redis.two-level-cache",
    name = "enabled",
    havingValue = "true")
public class TwoLevelCacheMessageListener implements MessageListener {

  private final TwoLevelCacheManager cacheManager;
  private final ObjectMapper objectMapper;
  private GenericJacksonJsonRedisSerializer serializer;

  private GenericJacksonJsonRedisSerializer getSerializer() {
    if (serializer == null) {
      serializer = new GenericJacksonJsonRedisSerializer(objectMapper);
    }
    return serializer;
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      Object deserialized = getSerializer().deserialize(message.getBody());
      if (deserialized instanceof TwoLevelCacheMessage cacheMessage) {
        cacheManager.clearLocal(cacheMessage.getCacheName(), cacheMessage.getKey());
      }
    } catch (Exception e) {
      log.error("Failed to parse cache invalidation message", e);
    }
  }
}
