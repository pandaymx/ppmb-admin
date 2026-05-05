package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@DisplayName("二级缓存管理器单元测试")
class TwoLevelCacheTest {

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
  @DisplayName("测试存放并从 L1 缓存读取")
  void testPutAndGet() {
    String cacheName = "testCache";
    String key = "testKey";
    String value = "testValue";

    cacheManager.put(cacheName, key, value, Duration.ofMinutes(1));

    Object retrievedFromL1 = cacheManager.get(cacheName, key);
    Assertions.assertEquals(value, retrievedFromL1, "应当从 L1 缓存命中数据");

    Mockito.verify(redisTemplate)
        .convertAndSend(
            Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));
  }

  @Test
  @DisplayName("测试 L1 未命中后从 L2 缓存读取并回填 L1")
  void testGetFromL2() {
    String cacheName = "testCache2";
    String key = "testKey2";
    String value = "testValue2";

    Mockito.when(valueOperations.get(cacheName + ":" + key)).thenReturn(value);

    Object retrieved = cacheManager.get(cacheName, key);
    Assertions.assertEquals(value, retrieved, "应当从 L2 缓存获取数据");

    // 验证第二次读取不再访问 Redis，证明已存入 L1
    cacheManager.get(cacheName, key);
    Mockito.verify(valueOperations, Mockito.times(1)).get(cacheName + ":" + key);
  }

  @Test
  @DisplayName("测试 L1 和 L2 均未命中")
  void testGetMissAll() {
    String cacheName = "testCacheMiss";
    String key = "testKeyMiss";

    Mockito.when(valueOperations.get(cacheName + ":" + key)).thenReturn(null);

    Object retrieved = cacheManager.get(cacheName, key);
    Assertions.assertNull(retrieved, "当 L1 和 L2 均未命中时应当返回 null");
  }

  @Test
  @DisplayName("测试失效缓存并发送广播消息")
  void testEvictAndClear() {
    String cacheName = "testCache3";
    String key = "testKey3";
    String value = "testValue3";

    // 存入 L1 和 L2
    cacheManager.put(cacheName, key, value, Duration.ofMinutes(1));
    Assertions.assertEquals(value, cacheManager.get(cacheName, key));

    // 失效 L2 并清除 L1
    cacheManager.evict(cacheName, key);
    Mockito.verify(redisTemplate).delete(cacheName + ":" + key);
    Mockito.verify(redisTemplate, Mockito.atLeastOnce())
        .convertAndSend(
            Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));

    // 专门清除本地缓存
    cacheManager.put(cacheName, key, value, Duration.ofMinutes(1));
    cacheManager.clearLocal(cacheName, key);

    // clearLocal 后 L1 应为空，触发 L2 读取
    Mockito.when(valueOperations.get(cacheName + ":" + key)).thenReturn(value);
    Object retrieved = cacheManager.get(cacheName, key);
    Assertions.assertEquals(value, retrieved);
    Mockito.verify(valueOperations, Mockito.atLeastOnce()).get(cacheName + ":" + key);
  }
}
