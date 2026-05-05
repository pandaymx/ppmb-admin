package top.ppmblszdp.common.redis.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@DisplayName("Redis 自动配置测试")
class PpmbRedisAutoConfigurationTest {

  @Test
  @DisplayName("测试默认 Bean 加载")
  void testDefaultBeans() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(PpmbRedisAutoConfiguration.class))
        .withUserConfiguration(TestConfig.class)
        .run(
            context -> {
              assertThat(context).hasBean("redisTemplate");
              assertThat(context).hasBean("stringRedisTemplate");
              assertThat(context).hasBean("objectMapper");
            });
  }

  @Test
  @DisplayName("测试 redisObjectMapper 配置")
  void testRedisObjectMapper() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(PpmbRedisAutoConfiguration.class))
        .withUserConfiguration(TestConfig.class)
        .run(
            context -> {
              assertThat(context).hasBean("redisObjectMapper");
              ObjectMapper mapper = context.getBean("redisObjectMapper", ObjectMapper.class);
              assertThat(mapper).isNotNull();
              // Jackson 3 默认支持 JSR310，无需单独验证模块
            });
  }

  @Test
  @DisplayName("测试 RedisMessageListenerContainer 加载")
  void testRedisMessageListenerContainer() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(PpmbRedisAutoConfiguration.class))
        .withUserConfiguration(
            TestConfig.class, ExtraMockConfig.class, DisableAutoStartupConfig.class)
        .withPropertyValues("ppmb.redis.two-level-cache.enabled=true")
        .run(
            context -> {
              assertThat(context).hasBean("redisMessageListenerContainer");
            });

    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(PpmbRedisAutoConfiguration.class))
        .withUserConfiguration(
            TestConfig.class, ExtraMockConfig.class, DisableAutoStartupConfig.class)
        .withPropertyValues("ppmb.redis.two-level-cache.enabled=false")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean("redisMessageListenerContainer");
            });
  }

  @Test
  @DisplayName("测试 RedisCacheManager 加载")
  void testRedisCacheManager() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(PpmbRedisAutoConfiguration.class))
        .withUserConfiguration(TestConfig.class)
        .run(
            context -> {
              assertThat(context).hasBean("redisCacheManager");
            });
  }

  @Configuration
  static class ExtraMockConfig {
    @Bean
    top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener listener() {
      return mock(top.ppmblszdp.common.redis.util.TwoLevelCacheMessageListener.class);
    }
  }

  @Configuration
  static class DisableAutoStartupConfig {
    @Bean
    static org.springframework.beans.factory.config.BeanPostProcessor disableAutoStartup() {
      return new org.springframework.beans.factory.config.BeanPostProcessor() {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
          if (bean
              instanceof org.springframework.data.redis.listener.RedisMessageListenerContainer) {
            ((org.springframework.data.redis.listener.RedisMessageListenerContainer) bean)
                .setAutoStartup(false);
          }
          return bean;
        }
      };
    }
  }

  @Configuration
  static class TestConfig {
    @Bean
    RedisConnectionFactory redisConnectionFactory() {
      RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
      org.springframework.data.redis.connection.RedisConnection connection =
          mock(org.springframework.data.redis.connection.RedisConnection.class);
      org.mockito.Mockito.when(factory.getConnection()).thenReturn(connection);
      return factory;
    }

    @Bean
    ObjectMapper objectMapper() {
      return JsonMapper.builder().build();
    }
  }
}
