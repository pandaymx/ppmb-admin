package top.ppmblszdp.common.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener;

public class PpmbRedisAutoConfigurationTest {

  @Test
  void testRedisTemplateConfiguration() {
    PpmbRedisAutoConfiguration config = new PpmbRedisAutoConfiguration();
    RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
    ObjectMapper objectMapper = new ObjectMapper();

    RedisTemplate<String, Object> template = config.redisTemplate(factory, objectMapper);

    Assertions.assertNotNull(template);
    Assertions.assertEquals(factory, template.getConnectionFactory());
    Assertions.assertTrue(template.getKeySerializer() instanceof RedisSerializer);
  }

  @Test
  void testRedisCacheManagerConfiguration() {
    PpmbRedisAutoConfiguration config = new PpmbRedisAutoConfiguration();
    RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
    ObjectMapper objectMapper = new ObjectMapper();

    RedisCacheManager cacheManager = config.redisCacheManager(factory, objectMapper);

    Assertions.assertNotNull(cacheManager);
  }

  @Test
  void testStringRedisTemplateConfiguration() {
    PpmbRedisAutoConfiguration config = new PpmbRedisAutoConfiguration();
    RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);

    StringRedisTemplate template = config.stringRedisTemplate(factory);

    Assertions.assertNotNull(template);
    Assertions.assertEquals(factory, template.getConnectionFactory());
  }

  @Test
  void testRedisMessageListenerContainerConfiguration() {
    PpmbRedisAutoConfiguration config = new PpmbRedisAutoConfiguration();
    RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
    TwoLevelCacheMessageListener listener = Mockito.mock(TwoLevelCacheMessageListener.class);

    RedisMessageListenerContainer container =
        config.redisMessageListenerContainer(factory, listener);

    Assertions.assertNotNull(container);
  }
}
