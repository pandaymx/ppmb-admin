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
@ConditionalOnProperty(
    prefix = "ppmb.redis.two-level-cache",
    name = "enabled",
    havingValue = "true")
public class TwoLevelCacheManager {

  private final RedisTemplate<String, Object> redisTemplate;

  private final ConcurrentMap<String, Cache<String, Object>> caffeineCaches =
      new ConcurrentHashMap<>();

  public static final String CACHE_TOPIC = "cache:invalidate:topic";

  private Cache<String, Object> getCaffeineCache(String cacheName) {
    return caffeineCaches.computeIfAbsent(
        cacheName,
        k ->
            Caffeine.newBuilder()
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
