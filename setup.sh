#!/bin/bash
set -e

# Update libs.versions.toml
cat gradle/libs.versions.toml | awk '
/^\[versions\]/ {
    print
    print "testcontainers = \"1.20.1\""
    print "caffeine = \"3.1.8\""
    next
}
/^\[libraries\]/ {
    print
    print "spring-boot-starter-data-redis = { group = \"org.springframework.boot\", name = \"spring-boot-starter-data-redis\" }"
    print "testcontainers-bom = { group = \"org.testcontainers\", name = \"testcontainers-bom\", version.ref = \"testcontainers\" }"
    print "testcontainers = { group = \"org.testcontainers\", name = \"testcontainers\" }"
    print "testcontainers-junit-jupiter = { group = \"org.testcontainers\", name = \"junit-jupiter\" }"
    print "spring-boot-testcontainers = { group = \"org.springframework.boot\", name = \"spring-boot-testcontainers\" }"
    print "caffeine = { group = \"com.github.ben-manes.caffeine\", name = \"caffeine\", version.ref = \"caffeine\" }"
    print "jacksonDatatypeJsr310 = { group = \"com.fasterxml.jackson.datatype\", name = \"jackson-datatype-jsr310\", version.ref = \"jackson\" }"
    next
}
{ print }
' > gradle/libs.versions.toml.tmp && mv gradle/libs.versions.toml.tmp gradle/libs.versions.toml

# Update settings.gradle.kts
echo 'include("ppmb-common-redis")' >> settings.gradle.kts

# Create directories
mkdir -p ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/{config,util} \
         ppmb-common-redis/src/main/resources/META-INF/spring \
         ppmb-common-redis/src/main/resources/lua \
         ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/{config,util}

# Create build.gradle.kts
cat << 'GRADLE' > ppmb-common-redis/build.gradle.kts
plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("testcontainers-bom").get()))

    implementation(project(":ppmb-common-api"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation(libs.findLibrary("spring-boot-starter-data-redis").get())
    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jacksonCore").get())
    implementation(libs.findLibrary("jacksonAnnotations").get())
    implementation(libs.findLibrary("jacksonDatatypeJsr310").get())
    implementation(libs.findLibrary("caffeine").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testImplementation(libs.findLibrary("testcontainers").get())
    testImplementation(libs.findLibrary("testcontainers-junit-jupiter").get())
    testImplementation(libs.findLibrary("spring-boot-testcontainers").get())

    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
}
GRADLE

# Create PpmbRedisAutoConfiguration.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/config/PpmbRedisAutoConfiguration.java
package top.ppmblszdp.common.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import top.ppmblszdp.common.redis.util.TwoLevelCacheManager;
import top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener;

@AutoConfiguration(before = DataRedisAutoConfiguration.class)
@ConditionalOnClass(RedisOperations.class)
public class PpmbRedisAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper redisObjectMapper(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.registerModule(new JavaTimeModule());
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);
    return redisObjectMapper;
  }

  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate")
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    var serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(RedisSerializer.string());
    template.setHashValueSerializer(serializer);
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  @ConditionalOnMissingBean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(factory);
    return template;
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "ppmb.redis.two-level-cache", name = "enabled", havingValue = "true")
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory factory, TwoLevelCacheMessageListener listener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(factory);
    container.addMessageListener(listener, new ChannelTopic(TwoLevelCacheManager.CACHE_TOPIC));
    return container;
  }

  @Bean
  @ConditionalOnMissingBean
  public RedisCacheManager redisCacheManager(
      RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    var serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues();

    return RedisCacheManager.builder(factory).cacheDefaults(config).build();
  }
}
JAVA

echo 'top.ppmblszdp.common.redis.config.PpmbRedisAutoConfiguration' > ppmb-common-redis/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

# Create LogicalExpirationWrapper.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/LogicalExpirationWrapper.java
package top.ppmblszdp.common.redis.util;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LogicalExpirationWrapper<T> {
  private T data;
  private LocalDateTime logicalExpire;
}
JAVA

# Create RedisRateLimiter.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/RedisRateLimiter.java
package top.ppmblszdp.common.redis.util;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ppmb.redis.rate-limiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisRateLimiter {

  private final StringRedisTemplate stringRedisTemplate;
  private DefaultRedisScript<Long> limitScript;

  /**
   * Checks if the request is allowed based on limit and time window.
   *
   * @param key the rate limit key
   * @param count the max allowed requests
   * @param period the time window in seconds
   * @return true if allowed, false if limit exceeded
   */
  public boolean isAllowed(String key, int count, int period) {
    if (limitScript == null) {
      limitScript = new DefaultRedisScript<>();
      limitScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/limit.lua")));
      limitScript.setResultType(Long.class);
    }

    Long result = stringRedisTemplate.execute(
        limitScript,
        Collections.singletonList(key),
        String.valueOf(count),
        String.valueOf(period));

    return result != null && result == 1L;
  }
}
JAVA

# Create RedisUtil.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/RedisUtil.java
package top.ppmblszdp.common.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String NULL_VALUE = "NULL_CACHE";
  private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
  private static final String LOCK_PREFIX = "lock:";
  private static final String ID_PREFIX = UUID.randomUUID().toString().replace("-", "") + "-";

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
      redisTemplate.opsForValue().set(key, NULL_VALUE, durationWithJitter);
    } else {
      redisTemplate.opsForValue().set(key, value, durationWithJitter);
    }
  }

  /** Adds a random jitter (between 0% and 10% of the duration) to prevent cache avalanche. */
  private Duration addJitter(Duration duration) {
    if (duration == null || duration.isZero() || duration.isNegative()) {
      return duration;
    }
    long millis = duration.toMillis();
    long jitterMillis = ThreadLocalRandom.current().nextLong(millis / 10 + 1);
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
    } catch (ClassCastException e) {
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
    return Boolean.TRUE.equals(
        redisTemplate.opsForValue().setIfAbsent(key, value != null ? value : NULL_VALUE, duration));
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
    String threadId = ID_PREFIX + Thread.currentThread().getId();
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
      unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/unlock.lua")));
      unlockScript.setResultType(Long.class);
    }
    String threadId = ID_PREFIX + Thread.currentThread().getId();
    redisTemplate.execute(unlockScript, Collections.singletonList(key), threadId);
  }

  /**
   * Retrieves data with logical expiration to prevent cache breakdown.
   *
   * @param <T> the type of the value
   * @param key the key
   * @param clazz the class of the value
   * @param id the id to pass to the db fallback
   * @param duration the duration for logical expire
   * @param fallback the database fallback function
   * @return the value
   */
  public <T, ID> T getWithLogicalExpire(
      String key, Class<T> clazz, ID id, Duration duration, Function<ID, T> fallback) {
    Object wrapperObj = redisTemplate.opsForValue().get(key);
    if (wrapperObj == null) {
      return null;
    }

    LogicalExpirationWrapper<?> wrapper = null;
    try {
        wrapper = objectMapper.convertValue(wrapperObj, LogicalExpirationWrapper.class);
    } catch(IllegalArgumentException e) {
        return null;
    }

    if (wrapper == null || wrapper.getData() == null) {
      return null;
    }

    T data = null;
    try {
        data = objectMapper.convertValue(wrapper.getData(), clazz);
    } catch(IllegalArgumentException e) {
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
JAVA

# Create TwoLevelCacheManager.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/TwoLevelCacheManager.java
package top.ppmblszdp.common.redis.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ppmb.redis.two-level-cache", name = "enabled", havingValue = "true")
public class TwoLevelCacheManager {

  private final RedisTemplate<String, Object> redisTemplate;

  private final ConcurrentMap<String, Cache<String, Object>> caffeineCaches = new ConcurrentHashMap<>();

  public static final String CACHE_TOPIC = "cache:invalidate:topic";

  private Cache<String, Object> getCaffeineCache(String cacheName) {
    return caffeineCaches.computeIfAbsent(
        cacheName,
        k -> Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build());
  }

  public Object get(String cacheName, String key) {
    Cache<String, Object> caffeineCache = getCaffeineCache(cacheName);
    Object value = caffeineCache.getIfPresent(key);
    if (value != null) {
      log.debug("L1 Cache hit for {}:{}", cacheName, key);
      return value;
    }

    String redisKey = cacheName + ":" + key;
    value = redisTemplate.opsForValue().get(redisKey);
    if (value != null) {
      log.debug("L2 Cache hit for {}:{}", cacheName, key);
      caffeineCache.put(key, value);
    }
    return value;
  }

  public void put(String cacheName, String key, Object value, Duration duration) {
    String redisKey = cacheName + ":" + key;
    redisTemplate.opsForValue().set(redisKey, value, duration);
    getCaffeineCache(cacheName).put(key, value);

    TwoLevelCacheMessage message = new TwoLevelCacheMessage(cacheName, key);
    redisTemplate.convertAndSend(CACHE_TOPIC, message);
  }

  public void evict(String cacheName, String key) {
    String redisKey = cacheName + ":" + key;
    redisTemplate.delete(redisKey);
    getCaffeineCache(cacheName).invalidate(key);

    TwoLevelCacheMessage message = new TwoLevelCacheMessage(cacheName, key);
    redisTemplate.convertAndSend(CACHE_TOPIC, message);
  }

  public void clearLocal(String cacheName, String key) {
    log.debug("Clearing L1 cache for {}:{}", cacheName, key);
    getCaffeineCache(cacheName).invalidate(key);
  }
}
JAVA

# Create TwoLevelCacheMessage.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/TwoLevelCacheMessage.java
package top.ppmblszdp.common.redis.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TwoLevelCacheMessage {
  private String cacheName;
  private String key;
}
JAVA

# Create TwoLevelCacheMessageListener.java
cat << 'JAVA' > ppmb-common-redis/src/main/java/top/ppmblszdp/common/redis/util/TwoLevelCacheMessageListener.java
package top.ppmblszdp.common.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ppmb.redis.two-level-cache", name = "enabled", havingValue = "true")
public class TwoLevelCacheMessageListener implements MessageListener {

  private final TwoLevelCacheManager cacheManager;
  private final ObjectMapper objectMapper;
  private GenericJackson2JsonRedisSerializer serializer;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      if (serializer == null) {
          serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
      }
      Object deserialized = serializer.deserialize(message.getBody());
      if (deserialized instanceof TwoLevelCacheMessage cacheMessage) {
        cacheManager.clearLocal(cacheMessage.getCacheName(), cacheMessage.getKey());
      }
    } catch (Exception e) {
      log.error("Failed to parse cache invalidation message", e);
    }
  }
}
JAVA

# Create LUA scripts
cat << 'LUA' > ppmb-common-redis/src/main/resources/lua/limit.lua
-- Sliding Window or Token Bucket could be used. Here is a simple fixed window limit.
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local current = tonumber(redis.call('get', key) or "0")

if current + 1 > limit then
    return 0
else
    redis.call("INCRBY", key, "1")
    -- ARGV[2] is expire time in seconds
    if current == 0 then
        redis.call("EXPIRE", key, ARGV[2])
    end
    return 1
end
LUA

cat << 'LUA' > ppmb-common-redis/src/main/resources/lua/unlock.lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
LUA

# Create tests
cat << 'JAVA' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/config/PpmbRedisAutoConfigurationTest.java
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

        RedisMessageListenerContainer container = config.redisMessageListenerContainer(factory, listener);

        Assertions.assertNotNull(container);
    }
}
JAVA

cat << 'JAVA' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisRateLimiterTest.java
package top.ppmblszdp.common.redis.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = RedisRateLimiterTest.TestApplication.class)
@Testcontainers
public class RedisRateLimiterTest {

    @SpringBootApplication(scanBasePackages = "top.ppmblszdp.common.redis")
    static class TestApplication {}

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("public.ecr.aws/docker/library/redis:7-alpine").asCompatibleSubstituteFor("redis"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
        registry.add("ppmb.redis.rate-limiter.enabled", () -> "true");
    }

    @Autowired
    private RedisRateLimiter rateLimiter;

    @Test
    void testRateLimiterConcurrent() throws InterruptedException {
        String key = "concurrentRateLimitKey";
        int count = 5;
        int period = 10;
        int numThreads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    if (rateLimiter.isAllowed(key, count, period)) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Assertions.assertEquals(count, successCount.get());
        Assertions.assertEquals(numThreads - count, failureCount.get());
    }
}
JAVA

cat << 'JAVA' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/RedisUtilTest.java
package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = RedisUtilTest.TestApplication.class)
@Testcontainers
public class RedisUtilTest {

    @SpringBootApplication(scanBasePackages = "top.ppmblszdp.common.redis")
    static class TestApplication {}

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("public.ecr.aws/docker/library/redis:7-alpine").asCompatibleSubstituteFor("redis"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testSetAndGet() {
        String key = "testKey";
        String value = "testValue";
        redisUtil.set(key, value, Duration.ofMinutes(1));

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(value, retrieved.get());
    }

    @Test
    void testSetAndGetWithTimeUnit() {
        String key = "testKeyTime";
        String value = "testValueTime";
        redisUtil.set(key, value, 60, TimeUnit.SECONDS);

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(value, retrieved.get());
    }

    @Test
    void testSetNull() {
        String key = "nullKey";
        redisUtil.set(key, null, Duration.ofMinutes(1));

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testSetNullWithTimeUnit() {
        String key = "nullKeyTime";
        redisUtil.set(key, null, 60, TimeUnit.SECONDS);

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testGetNonExistent() {
        Optional<String> retrieved = redisUtil.get("nonExistentKey", String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testDelete() {
        String key = "deleteKey";
        redisUtil.set(key, "value", Duration.ofMinutes(1));
        Assertions.assertTrue(redisUtil.get(key, String.class).isPresent());

        boolean deleted = redisUtil.delete(key);
        Assertions.assertTrue(deleted);
        Assertions.assertFalse(redisUtil.get(key, String.class).isPresent());
    }

    @Test
    void testSetIfAbsent() {
        String key = "absentKey";
        boolean firstSet = redisUtil.setIfAbsent(key, "value", Duration.ofMinutes(1));
        Assertions.assertTrue(firstSet);

        boolean secondSet = redisUtil.setIfAbsent(key, "value2", Duration.ofMinutes(1));
        Assertions.assertFalse(secondSet);
    }

    @Test
    void testSetIfAbsentNull() {
        String key = "absentNullKey";
        boolean firstSet = redisUtil.setIfAbsent(key, null, Duration.ofMinutes(1));
        Assertions.assertTrue(firstSet);

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testExpire() {
        String key = "expireKey";
        redisUtil.set(key, "value", Duration.ofMinutes(1));

        boolean expired = redisUtil.expire(key, Duration.ofMinutes(2));
        Assertions.assertTrue(expired);
    }

    @Test
    void testGetClassCastException() {
        String key = "castKey";
        redisTemplate.opsForValue().set(key, 12345);

        Optional<String> retrieved = redisUtil.get(key, String.class);
        Assertions.assertFalse(retrieved.isPresent());
    }

    @Test
    void testLogicalExpire() throws Exception {
        String key = "logicalExpireKey";
        String value = "logicalValue";

        redisUtil.setWithLogicalExpire(key, value, Duration.ofSeconds(2));

        String retrieved = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertEquals(value, retrieved);

        Thread.sleep(2500);

        String retrievedAfterExpire = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertEquals(value, retrievedAfterExpire); // returns stale data immediately

        Thread.sleep(200); // Wait for async rebuild

        String retrievedRefreshed = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertEquals("newValue", retrievedRefreshed);
    }

    @Test
    void testLogicalExpireNullOrInvalid() {
        String key = "logicalExpireNullKey";
        String retrieved = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertNull(retrieved);

        redisTemplate.opsForValue().set(key, "invalidWrapper");
        String retrievedInvalid = redisUtil.getWithLogicalExpire(key, String.class, 1L, Duration.ofSeconds(2), (id) -> "newValue");
        Assertions.assertNull(retrievedInvalid);
    }

    @Test
    void testSetIfAbsentConcurrent() throws InterruptedException {
        String key = "concurrentAbsentKey";
        int numThreads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    if (redisUtil.setIfAbsent(key, "value", Duration.ofMinutes(1))) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Assertions.assertEquals(1, successCount.get()); // Only one thread should successfully set it
    }
}
JAVA

cat << 'JAVA' > ppmb-common-redis/src/test/java/top/ppmblszdp/common/redis/util/TwoLevelCacheTest.java
package top.ppmblszdp.common.redis.util;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = TwoLevelCacheTest.TestApplication.class, properties = {"ppmb.redis.two-level-cache.enabled=true"})
@Testcontainers
public class TwoLevelCacheTest {

    @SpringBootApplication(scanBasePackages = "top.ppmblszdp.common.redis")
    static class TestApplication {}

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("public.ecr.aws/docker/library/redis:7-alpine").asCompatibleSubstituteFor("redis"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @Autowired
    private TwoLevelCacheManager cacheManager;

    @Test
    void testTwoLevelCache() throws InterruptedException {
        String cacheName = "testCache";
        String key = "testKey";
        String value = "testValue";

        cacheManager.put(cacheName, key, value, Duration.ofMinutes(1));

        Object retrieved = cacheManager.get(cacheName, key);
        Assertions.assertEquals(value, retrieved);

        // Wait for pub/sub message to clear local cache
        cacheManager.evict(cacheName, key);
        Thread.sleep(100);

        Object retrievedAfterEvict = cacheManager.get(cacheName, key);
        Assertions.assertNull(retrievedAfterEvict);
    }
}
JAVA

echo "Done"
