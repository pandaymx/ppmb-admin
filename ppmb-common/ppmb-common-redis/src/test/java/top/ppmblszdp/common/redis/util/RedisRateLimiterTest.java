package top.ppmblszdp.common.redis.util;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

class RedisRateLimiterTest {

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

    Mockito.when(
            stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))))
        .thenReturn(1L);

    Assertions.assertTrue(rateLimiter.isAllowed(key, count, period));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testIsNotAllowed() {
    String key = "testKey";
    int count = 5;
    int period = 60;

    Mockito.when(
            stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))))
        .thenReturn(0L);

    Assertions.assertFalse(rateLimiter.isAllowed(key, count, period));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testIsAllowedWithNullResult() {
    String key = "testKey";
    int count = 5;
    int period = 60;

    Mockito.when(
            stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))))
        .thenReturn(null);

    Assertions.assertFalse(rateLimiter.isAllowed(key, count, period));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testIsAllowedWithUnexpectedResult() {
    String key = "testKey";
    int count = 5;
    int period = 60;

    Mockito.when(
            stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.eq(Collections.singletonList(key)),
                ArgumentMatchers.eq(String.valueOf(count)),
                ArgumentMatchers.eq(String.valueOf(period))))
        .thenReturn(2L); // Unexpected result

    Assertions.assertFalse(rateLimiter.isAllowed(key, count, period));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testLimitScriptInitialization() {
    String key = "initKey";
    Mockito.when(
            stringRedisTemplate.execute(
                ArgumentMatchers.any(DefaultRedisScript.class),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()))
        .thenReturn(1L);

    // 第一次调用，初始化 limitScript
    rateLimiter.isAllowed(key, 5, 60);
    // 第二次调用，重用 limitScript
    rateLimiter.isAllowed(key, 5, 60);

    Mockito.verify(stringRedisTemplate, Mockito.times(2))
        .execute(
            ArgumentMatchers.any(DefaultRedisScript.class),
            ArgumentMatchers.anyList(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
  }
}
