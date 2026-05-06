package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tools.jackson.databind.ObjectMapper;

@DisplayName("Redis 工具类单元测试")
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
    objectMapper = tools.jackson.databind.json.JsonMapper.builder().findAndAddModules().build();
    redisUtil = new RedisUtil(redisTemplate, objectMapper);
  }

  @Test
  @DisplayName("测试存放并获取字符串")
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
  @DisplayName("测试存放并获取（带 TimeUnit 参数）")
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
  @DisplayName("测试存放 null 值（Duration 重载）")
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
  @DisplayName("测试存放 null 值（TimeUnit 重载）")
  void testSetNullWithTimeUnit() {
    String key = "nullKeyTime";
    redisUtil.set(key, null, 60, TimeUnit.SECONDS);
    Mockito.verify(valueOperations).set(key, "NULL_CACHE", 60, TimeUnit.SECONDS);

    Mockito.when(valueOperations.get(key)).thenReturn("NULL_CACHE");
    Optional<String> retrieved = redisUtil.get(key, String.class);
    Assertions.assertFalse(retrieved.isPresent());
  }

  @Test
  @DisplayName("测试获取不存在的键")
  void testGetNonExistent() {
    Mockito.when(valueOperations.get("nonExistentKey")).thenReturn(null);
    Optional<String> retrieved = redisUtil.get("nonExistentKey", String.class);
    Assertions.assertFalse(retrieved.isPresent());
  }

  @Test
  @DisplayName("测试删除键")
  void testDelete() {
    String key = "deleteKey";
    Mockito.when(redisTemplate.delete(key)).thenReturn(true);
    boolean deleted = redisUtil.delete(key);
    Assertions.assertTrue(deleted);
  }

  @Test
  @DisplayName("测试如果不存在则设置（setIfAbsent）")
  void testSetIfAbsent() {
    String key = "absentKey";
    Mockito.when(valueOperations.setIfAbsent(key, "value", Duration.ofMinutes(1))).thenReturn(true);
    boolean firstSet = redisUtil.setIfAbsent(key, "value", Duration.ofMinutes(1));
    Assertions.assertTrue(firstSet);
  }

  @Test
  @DisplayName("测试如果不存在则设置 null 值")
  void testSetIfAbsentNull() {
    String key = "absentNullKey";
    Mockito.when(valueOperations.setIfAbsent(key, "NULL_CACHE", Duration.ofMinutes(1)))
        .thenReturn(true);
    boolean firstSet = redisUtil.setIfAbsent(key, null, Duration.ofMinutes(1));
    Assertions.assertTrue(firstSet);
  }

  @Test
  @DisplayName("测试设置过期时间")
  void testExpire() {
    String key = "expireKey";
    Mockito.when(redisTemplate.expire(key, Duration.ofMinutes(2))).thenReturn(true);
    boolean expired = redisUtil.expire(key, Duration.ofMinutes(2));
    Assertions.assertTrue(expired);
  }

  @Test
  @DisplayName("测试获取时发生类型转换异常")
  void testGetClassCastException() {
    String key = "castKey";
    Mockito.when(valueOperations.get(key)).thenReturn(12345); // 返回 Integer 导致 ClassCastException

    Optional<String> retrieved = redisUtil.get(key, String.class);
    Assertions.assertFalse(retrieved.isPresent(), "类型不匹配时应返回 Optional.empty");
  }

  @Test
  @DisplayName("测试逻辑过期获取：未过期分支")
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
  @DisplayName("测试逻辑过期获取：已过期分支（触发异步重建）")
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
  @DisplayName("测试逻辑过期获取：各类空值及无效包装类分支")
  void testLogicalExpireNullOrInvalid() {
    String key = "logicalExpireNullKey";
    // wrapperObj == null
    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertNull(retrieved);

    // convertValue 异常
    Mockito.when(valueOperations.get(key)).thenReturn("invalidWrapper");
    String retrievedInvalid =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertNull(retrievedInvalid);

    // wrapper.getData() == null
    LogicalExpirationWrapper<Object> nullDataWrapper = new LogicalExpirationWrapper<>();
    nullDataWrapper.setData(null);
    nullDataWrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(60));
    Mockito.when(valueOperations.get(key)).thenReturn(nullDataWrapper);
    String retrievedNullData =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertNull(retrievedNullData);

    // clazz.cast 转换异常 (通过 objectMapper.convertValue 触发)
    LogicalExpirationWrapper<Object> wrongTypeWrapper = new LogicalExpirationWrapper<>();
    wrongTypeWrapper.setData("not-a-number");
    wrongTypeWrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(60));
    Mockito.when(valueOperations.get(key)).thenReturn(wrongTypeWrapper);
    Integer retrievedWrongType =
        redisUtil.getWithLogicalExpire(key, Integer.class, 1L, Duration.ofSeconds(2), id -> 456);
    Assertions.assertNull(retrievedWrongType, "数据类型不匹配时应返回 null");
  }

  @Test
  @DisplayName("测试 Duration 为 null 时的 set 处理")
  void testSetNullDuration() {
    String key = "nullDurationKey";
    redisUtil.set(key, "value", (Duration) null);
    Mockito.verify(valueOperations).set(key, "value");

    redisUtil.setIfAbsent(key, "value", null);
    Mockito.verify(valueOperations).setIfAbsent(key, "value");
  }

  @Test
  @DisplayName("测试逻辑过期设置：数据为 null 分支")
  void testSetWithLogicalExpireNull() {
    String key = "logicalNullKey";
    redisUtil.setWithLogicalExpire(key, null, Duration.ofMinutes(5));
    Mockito.verify(valueOperations)
        .set(Mockito.eq(key), Mockito.any(LogicalExpirationWrapper.class));
  }

  @Test
  @DisplayName("测试抖动函数：负值及零值分支")
  void testAddJitterCoverage() throws Exception {
    var method = RedisUtil.class.getDeclaredMethod("addJitter", Duration.class);
    method.setAccessible(true);

    // null 分支
    Duration nullDuration = (Duration) method.invoke(redisUtil, (Duration) null);
    Assertions.assertNull(nullDuration);

    // zero 分支
    Duration zeroDuration = Duration.ZERO;
    Duration jitterZero = (Duration) method.invoke(redisUtil, zeroDuration);
    Assertions.assertEquals(zeroDuration, jitterZero);

    // negative 分支
    Duration negativeDuration = Duration.ofMinutes(-1);
    Duration jitterNegative = (Duration) method.invoke(redisUtil, negativeDuration);
    Assertions.assertEquals(negativeDuration, jitterNegative);

    // positive 分支
    Duration positive = Duration.ofMinutes(10);
    Duration jittered = (Duration) method.invoke(redisUtil, positive);
    Assertions.assertTrue(jittered.toMillis() >= positive.toMillis());
    Assertions.assertTrue(jittered.toMillis() <= positive.toMillis() * 1.1 + 1);
  }

  @Test
  @DisplayName("测试释放锁（验证懒加载及执行）")
  void testUnlockLazyInit() {
    String key = "unlockKey";

    // 触发已过期分支以执行异步重建并调用 unlock
    LogicalExpirationWrapper<Object> mockWrapper = new LogicalExpirationWrapper<>();
    mockWrapper.setData("oldValue");
    mockWrapper.setLogicalExpire(LocalDateTime.now().minusSeconds(60));
    Mockito.when(valueOperations.get(key)).thenReturn(mockWrapper);

    String lockKey = "lock:" + key;
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.eq(lockKey), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(true);

    redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");

    org.awaitility.Awaitility.await()
        .atMost(1, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Mockito.verify(redisTemplate)
                    .execute(Mockito.any(), Mockito.anyList(), Mockito.anyString()));
  }

  @Test
  @DisplayName("测试 setIfAbsent null 值且无过期时间")
  void testSetIfAbsentNullNoDuration() {
    String key = "setIfAbsentNullKey";
    Mockito.when(valueOperations.setIfAbsent(key, "NULL_CACHE")).thenReturn(true);
    boolean result = redisUtil.setIfAbsent(key, null, null);
    Assertions.assertTrue(result);
  }

  @Test
  @DisplayName("测试 setIfAbsent 非 null 值且无过期时间")
  void testSetIfAbsentValueNoDuration() {
    String key = "setIfAbsentValNoDur";
    Mockito.when(valueOperations.setIfAbsent(key, "value")).thenReturn(true);
    boolean result = redisUtil.setIfAbsent(key, "value", null);
    Assertions.assertTrue(result);
  }

  @Test
  @DisplayName("测试 set 非 null 值且无过期时间（Duration 重载）")
  void testSetValueNoDuration() {
    String key = "setValNoDur";
    redisUtil.set(key, "value", (Duration) null);
    Mockito.verify(valueOperations).set(key, "value");
  }

  @Test
  @DisplayName("测试 setIfAbsent null 值且有过期时间")
  void testSetIfAbsentNullWithDuration() {
    String key = "setIfAbsentNullWithDur";
    Duration duration = Duration.ofMinutes(1);
    Mockito.when(valueOperations.setIfAbsent(key, "NULL_CACHE", duration)).thenReturn(true);
    boolean result = redisUtil.setIfAbsent(key, null, duration);
    Assertions.assertTrue(result);
  }

  @Test
  @DisplayName("测试 set null 值且无过期时间（Duration 重载）")
  void testSetNullNoDuration() {
    String key = "setNullNoDur";
    redisUtil.set(key, null, (Duration) null);
    Mockito.verify(valueOperations).set(key, "NULL_CACHE");
  }

  @Test
  @DisplayName("测试逻辑过期获取：过期时间为 null 分支")
  void testGetWithLogicalExpire_NullExpireTime() {
    String key = "nullExpireTimeKey";
    String value = "value";

    LogicalExpirationWrapper<Object> wrapper = new LogicalExpirationWrapper<>();
    wrapper.setData(value);
    wrapper.setLogicalExpire(null); // Null expire time

    Mockito.when(valueOperations.get(key)).thenReturn(wrapper);
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(true);

    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertEquals(value, retrieved);
  }

  @Test
  @DisplayName("测试逻辑过期获取：获取锁失败分支")
  void testGetWithLogicalExpire_LockFailed() {
    String key = "lockFailedKey";
    String value = "oldValue";

    LogicalExpirationWrapper<Object> wrapper = new LogicalExpirationWrapper<>();
    wrapper.setData(value);
    wrapper.setLogicalExpire(LocalDateTime.now().minusSeconds(60));

    Mockito.when(valueOperations.get(key)).thenReturn(wrapper);
    // 获取锁失败
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(false);

    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertEquals(value, retrieved);
    // 验证没有触发异步重建（这里比较难直接验证 Executor，但可以验证 unlock 没有被调用）
    Mockito.verify(redisTemplate, Mockito.never())
        .execute(Mockito.any(), Mockito.anyList(), Mockito.anyString());
  }

  @Test
  @DisplayName("测试删除键 (返回 null)")
  void testDeleteNull() {
    Mockito.when(redisTemplate.delete("key")).thenReturn(null);
    Assertions.assertFalse(redisUtil.delete("key"));
  }

  @Test
  @DisplayName("测试设置过期时间 (返回 null)")
  void testExpireNull() {
    Mockito.when(redisTemplate.expire(Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(null);
    Assertions.assertFalse(redisUtil.expire("key", Duration.ofMinutes(1)));
  }

  @Test
  @DisplayName("测试逻辑过期获取：获取锁返回 null 分支")
  void testGetWithLogicalExpire_LockNull() {
    String key = "lockNullKey";
    String value = "oldValue";

    LogicalExpirationWrapper<Object> wrapper = new LogicalExpirationWrapper<>();
    wrapper.setData(value);
    wrapper.setLogicalExpire(LocalDateTime.now().minusSeconds(60));

    Mockito.when(valueOperations.get(key)).thenReturn(wrapper);
    // 获取锁返回 null
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(null);

    String retrieved =
        redisUtil.getWithLogicalExpire(
            key, String.class, 1L, Duration.ofSeconds(2), id -> "newValue");
    Assertions.assertEquals(value, retrieved);
  }

  @Test
  @DisplayName("测试逻辑过期获取：异步重建发生异常")
  void testGetWithLogicalExpire_RebuildException() {
    String key = "rebuildExcKey";
    String value = "oldValue";

    LogicalExpirationWrapper<Object> wrapper = new LogicalExpirationWrapper<>();
    wrapper.setData(value);
    wrapper.setLogicalExpire(LocalDateTime.now().minusSeconds(60));

    Mockito.when(valueOperations.get(key)).thenReturn(wrapper);
    Mockito.when(
            valueOperations.setIfAbsent(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(Duration.class)))
        .thenReturn(true);

    // 触发重建并抛出异常
    redisUtil.getWithLogicalExpire(
        key,
        String.class,
        1L,
        Duration.ofSeconds(2),
        id -> {
          throw new RuntimeException("fallback error");
        });

    // 验证 unlock 依然被调用
    org.awaitility.Awaitility.await()
        .atMost(1, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Mockito.verify(redisTemplate)
                    .execute(Mockito.any(), Mockito.anyList(), Mockito.anyString()));
  }
}
