package top.ppmblszdp.common.redis.util;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String NULL_VALUE = "NULL_CACHE";
  private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
  private static final String LOCK_PREFIX = "lock:";
  private static final String ID_PREFIX = UUID.randomUUID().toString().replace("-", "") + "-";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private DefaultRedisScript<Long> unlockScript;

  /**
   * Sets a key-value pair with a timeout. Applying random jitter not recommended here as duration
   * varies by unit. Prefer using duration method for full avalanche protection.
   *
   * @param key the key
   * @param value the value
   * @param timeout the timeout
   * @param unit the time unit
   */
  public void set(String key, Object value, long timeout, TimeUnit unit) {
    if (value == null) {
      redisTemplate.opsForValue().set(key, NULL_VALUE, timeout, unit);
    } else {
      redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
  }

  /**
   * Sets a key-value pair with a duration timeout, applying a random jitter to prevent cache
   * avalanche.
   *
   * @param key the key
   * @param value the value
   * @param duration the duration
   */
  public void set(String key, Object value, Duration duration) {
    Duration durationWithJitter = addJitter(duration);
    if (value == null) {
      if (durationWithJitter == null) {
        redisTemplate.opsForValue().set(key, NULL_VALUE);
      } else {
        redisTemplate.opsForValue().set(key, NULL_VALUE, durationWithJitter);
      }
    } else {
      if (durationWithJitter == null) {
        redisTemplate.opsForValue().set(key, value);
      } else {
        redisTemplate.opsForValue().set(key, value, durationWithJitter);
      }
    }
  }

  /** Adds a random jitter (between 0% and 10% of the duration) to prevent cache avalanche. */
  private Duration addJitter(Duration duration) {
    if (duration == null) {
      return null;
    }
    if (duration.isZero() || duration.isNegative()) {
      return duration;
    }
    long millis = duration.toMillis();
    long jitterMillis = SECURE_RANDOM.nextLong(millis / 10 + 1);
    return Duration.ofMillis(millis + jitterMillis);
  }

  /**
   * Retrieves a value by key. Handles the anti-penetration NULL_VALUE.
   *
   * @param <T> the type of the value
   * @param key the key
   * @param clazz the class of the value
   * @return an optional containing the value if it exists, empty otherwise
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(String key, Class<T> clazz) {
    Object value = redisTemplate.opsForValue().get(key);
    if (value == null || NULL_VALUE.equals(value)) {
      return Optional.empty();
    }
    try {
      return Optional.of(clazz.cast(value));
    } catch (ClassCastException _) {
      return Optional.empty();
    }
  }

  /**
   * Deletes a key.
   *
   * @param key the key
   * @return true if the key was deleted, false otherwise
   */
  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

  /**
   * Sets a key only if it does not exist (useful for simple distributed locks).
   *
   * @param key the key
   * @param value the value
   * @param duration the duration
   * @return true if the key was set, false otherwise
   */
  public boolean setIfAbsent(String key, Object value, Duration duration) {
    Object val = value != null ? value : NULL_VALUE;
    if (duration == null) {
      return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, val));
    }
    return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, val, duration));
  }

  /**
   * Extends the timeout of a key.
   *
   * @param key the key
   * @param duration the duration
   * @return true if the timeout was extended, false otherwise
   */
  public boolean expire(String key, Duration duration) {
    return Boolean.TRUE.equals(redisTemplate.expire(key, duration));
  }

  /**
   * Sets a key-value pair with logical expiration.
   *
   * @param key the key
   * @param value the value
   * @param duration the duration after which it logically expires
   */
  public void setWithLogicalExpire(String key, Object value, Duration duration) {
    LogicalExpirationWrapper<Object> wrapper = new LogicalExpirationWrapper<>();
    wrapper.setData(value);
    wrapper.setLogicalExpire(LocalDateTime.now().plusSeconds(duration.getSeconds()));
    redisTemplate.opsForValue().set(key, wrapper);
  }

  /**
   * Tries to acquire a lock.
   *
   * @param key the lock key
   * @return true if acquired, false otherwise
   */
  private boolean tryLock(String key) {
    String threadId = ID_PREFIX + Thread.currentThread().threadId();
    Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, threadId, Duration.ofSeconds(10));
    return Boolean.TRUE.equals(flag);
  }

  /**
   * Releases a lock safely using a Lua script.
   *
   * @param key the lock key
   */
  private void unlock(String key) {
    if (unlockScript == null) {
      unlockScript = new DefaultRedisScript<>();
      unlockScript.setScriptSource(
          new ResourceScriptSource(new ClassPathResource("lua/unlock.lua")));
      unlockScript.setResultType(Long.class);
    }
    String threadId = ID_PREFIX + Thread.currentThread().threadId();
    redisTemplate.execute(unlockScript, Collections.singletonList(key), threadId);
  }

  /**
   * Retrieves data with logical expiration to prevent cache breakdown.
   *
   * @param <T> the type of the value
   * @param <I> the type of the identifier
   * @param key the key
   * @param clazz the class of the value
   * @param id the id to pass to the db fallback
   * @param duration the duration for logical expire
   * @param fallback the database fallback function
   * @return the value
   */
  public <T, I> T getWithLogicalExpire(
      String key, Class<T> clazz, I id, Duration duration, Function<I, T> fallback) {
    Object wrapperObj = redisTemplate.opsForValue().get(key);
    if (wrapperObj == null) {
      return null;
    }

    LogicalExpirationWrapper<?> wrapper = null;
    try {
      wrapper = objectMapper.convertValue(wrapperObj, LogicalExpirationWrapper.class);
    } catch (Exception _) {
      return null;
    }

    if (wrapper == null || wrapper.getData() == null) {
      return null;
    }

    T data = null;
    try {
      data = objectMapper.convertValue(wrapper.getData(), clazz);
    } catch (Exception _) {
      return null;
    }

    LocalDateTime expireTime = wrapper.getLogicalExpire();

    if (expireTime != null && expireTime.isAfter(LocalDateTime.now())) {
      return data;
    }
    String lockKey = LOCK_PREFIX + key;
    boolean isLock = tryLock(lockKey);
    if (isLock) {
      CACHE_REBUILD_EXECUTOR.submit(
          () -> {
            try {
              T newObj = fallback.apply(id);
              this.setWithLogicalExpire(key, newObj, duration);
            } finally {
              unlock(lockKey);
            }
          });
    }
    return data;
  }
}
