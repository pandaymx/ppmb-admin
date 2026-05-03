package top.ppmblszdp.common.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisUtilTest {

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
    Mockito.verify(valueOperations)
        .set(Mockito.eq(key), Mockito.eq(value), Mockito.any(Duration.class));

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
    Mockito.verify(valueOperations)
        .set(Mockito.eq(key), Mockito.eq("NULL_CACHE"), Mockito.any(Duration.class));

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
    Mockito.when(valueOperations.setIfAbsent(key, "NULL_CACHE", Duration.ofMinutes(1)))
        .thenReturn(true);
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

    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
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
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(true);

    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertEquals(value, retrieved);

    org.awaitility.Awaitility.await()
        .atMost(1, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Mockito.verify(valueOperations)
                    .setIfAbsent(
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)));
  }

  @Test
  void testLogicalExpireNullOrInvalid() {
    String key = "logicalExpireNullKey";
    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertNull(retrieved);

    Mockito.when(valueOperations.get(key)).thenReturn("invalidWrapper");
    String retrievedInvalid =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertNull(retrievedInvalid);

    LogicalExpirationWrapper<Object> invalidWrapper = new LogicalExpirationWrapper<>();
    invalidWrapper.setData("invalid");
    invalidWrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(60));

    Mockito.when(valueOperations.get(key)).thenReturn(invalidWrapper);
    // Integer retrievedCastEx =
    //    redisUtil.getWithLogicalExpire(key, Integer.class, 1L, Duration.ofSeconds(2), id -> 5);
    // Assertions.assertNull(retrievedCastEx);
  }

  @Test
  void testSetNullDuration() {
    String key = "nullDurationKey";
    redisUtil.set(key, "value", (Duration) null);
    Mockito.verify(valueOperations).set(Mockito.eq(key), Mockito.eq("value"));

    redisUtil.setIfAbsent(key, "value", null);
    Mockito.verify(valueOperations).setIfAbsent(Mockito.eq(key), Mockito.eq("value"));
  }

  @Test
  void testAddJitterCoverage() throws Exception {
    var method = RedisUtil.class.getDeclaredMethod("addJitter", Duration.class);
    method.setAccessible(true);

    Duration nullDuration = (Duration) method.invoke(redisUtil, (Duration) null);
    Assertions.assertNull(nullDuration);

    Duration zeroDuration = Duration.ZERO;
    Duration jitterZero = (Duration) method.invoke(redisUtil, zeroDuration);
    Assertions.assertEquals(zeroDuration, jitterZero);

    Duration positive = Duration.ofMinutes(10);
    Duration jittered = (Duration) method.invoke(redisUtil, positive);
    Assertions.assertTrue(jittered.toMillis() >= positive.toMillis());
    Assertions.assertTrue(jittered.toMillis() <= positive.toMillis() * 1.1 + 1);
  }
}
