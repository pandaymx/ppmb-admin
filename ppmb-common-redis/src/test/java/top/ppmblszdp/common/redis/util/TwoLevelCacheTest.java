package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class TwoLevelCacheTest {

  private RedisTemplate<String, Object> redisTemplate;
  private ValueOperations<String, Object> valueOperations;
  private TwoLevelCacheManager cacheManager;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    redisTemplate = Mockito.mock(RedisTemplate.class);
    valueOperations = Mockito.mock(ValueOperations.class);
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    cacheManager = new TwoLevelCacheManager(redisTemplate);
  }

  @Test
  void testPutAndGet() {
    String cacheName = "testCache";
    String key = "testKey";
    String value = "testValue";

    cacheManager.put(cacheName, key, value, Duration.ofMinutes(1));

    Object retrievedFromL1 = cacheManager.get(cacheName, key);
    Assertions.assertEquals(value, retrievedFromL1);

    Mockito.verify(redisTemplate)
        .convertAndSend(
            Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));
  }

  @Test
  void testGetFromL2() {
    String cacheName = "testCache2";
    String key = "testKey2";
    String value = "testValue2";

    Mockito.when(valueOperations.get(cacheName + ":" + key)).thenReturn(value);

    Object retrieved = cacheManager.get(cacheName, key);
    Assertions.assertEquals(value, retrieved);

    cacheManager.get(cacheName, key);
    Mockito.verify(valueOperations, Mockito.times(1)).get(cacheName + ":" + key);
  }

  @Test
  void testEvictAndClear() {
    String cacheName = "testCache3";
    String key = "testKey3";

    cacheManager.evict(cacheName, key);
    Mockito.verify(redisTemplate).delete(cacheName + ":" + key);
    Mockito.verify(redisTemplate)
        .convertAndSend(
            Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));

    cacheManager.clearLocal(cacheName, key);
  }
}
