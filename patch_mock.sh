cat << 'MOCK_TEST' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/TwoLevelCacheTest.java
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

        Mockito.verify(redisTemplate).convertAndSend(Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));
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
        Mockito.verify(redisTemplate).convertAndSend(Mockito.eq(TwoLevelCacheManager.CACHE_TOPIC), Mockito.any(TwoLevelCacheMessage.class));

        cacheManager.clearLocal(cacheName, key);
    }
}
MOCK_TEST

cat << 'MOCK_TEST' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisRateLimiterTest.java
package top.ppmblszdp.common.redis.util;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

public class RedisRateLimiterTest {

    private StringRedisTemplate stringRedisTemplate;
    private RedisRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);
        rateLimiter = new RedisRateLimiter(stringRedisTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsAllowed() {
        String key = "testKey";
        int count = 5;
        int period = 60;

        Mockito.when(stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))
        )).thenReturn(1L);

        Assertions.assertTrue(rateLimiter.isAllowed(key, count, period));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsNotAllowed() {
        String key = "testKey";
        int count = 5;
        int period = 60;

        Mockito.when(stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))
        )).thenReturn(0L);

        Assertions.assertFalse(rateLimiter.isAllowed(key, count, period));
    }
}
MOCK_TEST

cat << 'MOCK_TEST' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java
package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RedisUtilTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private RedisUtil redisUtil;
    private ObjectMapper objectMapper;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = Mockito.mock(RedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        redisUtil = new RedisUtil(redisTemplate, objectMapper);
    }

    @Test
    void testSetAndGet() {
        String key = "testKey";
        String value = "testValue";

        redisUtil.set(key, value, Duration.ofMinutes(1));
        Mockito.verify(valueOperations).set(Mockito.eq(key), Mockito.eq(value), Mockito.any(Duration.class));

        Mockito.when(valueOperations.get(key)).thenReturn(value);
        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(value, retrieved.get());
    }

    @Test
    void testSetAndGetWithTimeUnit() {
        String key = "testKeyTime";
        String value = "testValueTime";

        redisUtil.set(key, value, 60, TimeUnit.SECONDS);
        Mockito.verify(valueOperations).set(key, value, 60, TimeUnit.SECONDS);

        Mockito.when(valueOperations.get(key)).thenReturn(value);
        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(value, retrieved.get());
    }

    @Test
    void testSetNull() {
        String key = "nullKey";
        redisUtil.set(key, null, Duration.ofMinutes(1));
        Mockito.verify(valueOperations).set(Mockito.eq(key), Mockito.eq("NULL_CACHE"), Mockito.any(Duration.class));

        Mockito.when(valueOperations.get(key)).thenReturn("NULL_CACHE");
        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testSetNullWithTimeUnit() {
        String key = "nullKeyTime";
        redisUtil.set(key, null, 60, TimeUnit.SECONDS);
        Mockito.verify(valueOperations).set(key, "NULL_CACHE", 60, TimeUnit.SECONDS);

        Mockito.when(valueOperations.get(key)).thenReturn("NULL_CACHE");
        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testGetNonExistent() {
        Mockito.when(valueOperations.get("nonExistentKey")).thenReturn(null);
        Optional<String> retrieved = redisUtil.get("nonExistentKey", String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testDelete() {
        String key = "deleteKey";
        Mockito.when(redisTemplate.delete(key)).thenReturn(true);
        boolean deleted = redisUtil.delete(key);
        Assertions.assertTrue(deleted);
    }

    @Test
    void testSetIfAbsent() {
        String key = "absentKey";
        Mockito.when(valueOperations.setIfAbsent(key, "value", Duration.ofMinutes(1))).thenReturn(true);
        boolean firstSet = redisUtil.setIfAbsent(key, "value", Duration.ofMinutes(1));
        Assertions.assertTrue(firstSet);
    }

    @Test
    void testSetIfAbsentNull() {
        String key = "absentNullKey";
        Mockito.when(valueOperations.setIfAbsent(key, "NULL_CACHE", Duration.ofMinutes(1))).thenReturn(true);
        boolean firstSet = redisUtil.setIfAbsent(key, null, Duration.ofMinutes(1));
        Assertions.assertTrue(firstSet);
    }

    @Test
    void testExpire() {
        String key = "expireKey";
        Mockito.when(redisTemplate.expire(key, Duration.ofMinutes(2))).thenReturn(true);
        boolean expired = redisUtil.expire(key, Duration.ofMinutes(2));
        Assertions.assertTrue(expired);
    }

    @Test
    void testGetClassCastException() {
        String key = "castKey";
        Mockito.when(valueOperations.get(key)).thenReturn(12345); // Return an Integer

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testLogicalExpireMock() throws Exception {
        String key = "logicalExpireKey";
        String value = "logicalValue";

        LogicalExpirationWrapper<Object> mockWrapper = new LogicalExpirationWrapper<>();
        mockWrapper.setData(value);
        mockWrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(60));

        Map<String, Object> map = objectMapper.convertValue(mockWrapper, Map.class);
        Mockito.when(valueOperations.get(key)).thenReturn(map);

        String retrieved = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertEquals(value, retrieved);
    }

    @Test
    void testLogicalExpireRebuild() {
        String key = "logicalExpireRebuildKey";
        String value = "logicalValue";

        LogicalExpirationWrapper<Object> mockWrapper = new LogicalExpirationWrapper<>();
        mockWrapper.setData(value);
        mockWrapper.setLogicalExpire(LocalDateTime.now().minusSeconds(60));

        Map<String, Object> map = objectMapper.convertValue(mockWrapper, Map.class);

        Mockito.when(valueOperations.get(key)).thenReturn(map);
        Mockito.when(valueOperations.setIfAbsent(Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class))).thenReturn(true);

        String retrieved = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertEquals(value, retrieved);

        try { Thread.sleep(200); } catch(InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @Test
    void testLogicalExpireNullOrInvalid() {
        String key = "logicalExpireNullKey";
        String retrieved = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertNull(retrieved);

        Mockito.when(valueOperations.get(key)).thenReturn("invalidWrapper");
        String retrievedInvalid = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertNull(retrievedInvalid);

        LogicalExpirationWrapper<Object> mockInvalidData = new LogicalExpirationWrapper<>();
        mockInvalidData.setData(12345); // Invalid data type
        mockInvalidData.setLogicalExpire(LocalDateTime.now().plusSeconds(60));
        Map<String, Object> map = objectMapper.convertValue(mockInvalidData, Map.class);

        Mockito.when(valueOperations.get(key)).thenReturn(map);
        String retrievedCastEx = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertNull(retrievedCastEx);
    }
}
MOCK_TEST
