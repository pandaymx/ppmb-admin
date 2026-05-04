package top.ppmblszdp.common.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import top.ppmblszdp.common.redis.serializer.Jackson3JsonRedisSerializer;
import top.ppmblszdp.common.redis.util.TwoLevelCacheManager;
import top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener;

@AutoConfiguration(
    beforeName = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration")
@ConditionalOnClass(RedisOperations.class)
public class PpmbRedisAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = "redisObjectMapper")
  public ObjectMapper redisObjectMapper(
      @org.springframework.beans.factory.annotation.Qualifier("objectMapper") ObjectMapper objectMapper) {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType("top.ppmblszdp.")
            .allowIfBaseType("java.util.")
            .allowIfBaseType("java.time.")
            .allowIfBaseType(Object.class)
            .build();
    return JsonMapper.builder()
        .activateDefaultTyping(
            ptv, tools.jackson.databind.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
        .build();
  }

  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate")
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory factory,
      @org.springframework.beans.factory.annotation.Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    Jackson3JsonRedisSerializer<Object> serializer =
        new Jackson3JsonRedisSerializer<>(redisObjectMapper, Object.class);
    // In newer Spring Data Redis versions, we might need a different way to set ObjectMapper
    // but this is the most compatible one if direct constructor fails.

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
  public RedisCacheManager redisCacheManager(
      RedisConnectionFactory factory,
      @org.springframework.beans.factory.annotation.Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
    Jackson3JsonRedisSerializer<Object> serializer =
        new Jackson3JsonRedisSerializer<>(redisObjectMapper, Object.class);

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
