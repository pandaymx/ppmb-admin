package top.ppmblszdp.common.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import top.ppmblszdp.common.redis.util.TwoLevelCacheManager;
import top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener;

@AutoConfiguration(
    beforeName = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration")
@ConditionalOnClass(RedisOperations.class)
public class PpmbRedisAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper redisObjectMapper(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.registerModule(new JavaTimeModule());
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType("top.ppmblszdp.")
            .allowIfBaseType("java.util.")
            .allowIfBaseType("java.time.")
            .allowIfBaseType(Object.class)
            .build();
    redisObjectMapper.activateDefaultTyping(
        ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    return redisObjectMapper;
  }

  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate")
  @SuppressWarnings("removal")
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    RedisSerializer<Object> serializer =
        new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer(
            redisObjectMapper);

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
  @ConditionalOnProperty(
      prefix = "ppmb.redis.two-level-cache",
      name = "enabled",
      havingValue = "true")
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory factory, TwoLevelCacheMessageListener listener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(factory);
    container.addMessageListener(listener, new ChannelTopic(TwoLevelCacheManager.CACHE_TOPIC));
    return container;
  }

  @Bean
  @ConditionalOnMissingBean
  @SuppressWarnings("removal")
  public RedisCacheManager redisCacheManager(
      RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    RedisSerializer<Object> serializer =
        new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer(
            redisObjectMapper);
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
