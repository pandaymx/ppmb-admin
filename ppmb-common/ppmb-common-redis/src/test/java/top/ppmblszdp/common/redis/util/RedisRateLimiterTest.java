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
}
