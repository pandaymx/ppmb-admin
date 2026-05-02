package top.ppmblszdp.common.mq;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@DisplayName("RabbitMQ 配置类测试")
class RabbitMqConfigTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RabbitMqConfig.class));

  @Test
  @DisplayName("测试 Bean 的自动配置")
  void testAutoConfiguration() {
    contextRunner
        .withUserConfiguration(TestConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(MessageConverter.class);
              assertThat(context)
                  .getBean(MessageConverter.class)
                  .isInstanceOf(Jackson2JsonMessageConverter.class);
              assertThat(context).hasBean("rabbitRetryTemplate");
              assertThat(context).getBean("rabbitRetryTemplate").isInstanceOf(RetryTemplate.class);
            });
  }

  @Test
  @DisplayName("当已有 MessageConverter 时不应重新创建")
  void testConditionalOnMissingBean() {
    contextRunner
        .withUserConfiguration(ExistingBeanConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(MessageConverter.class);
              assertThat(context)
                  .getBean(MessageConverter.class)
                  .isSameAs(ExistingBeanConfig.CUSTOM_CONVERTER);
            });
  }

  @Configuration
  static class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
      return null; // Just to satisfy ConditionalOnClass
    }
  }

  @Configuration
  static class ExistingBeanConfig extends TestConfig {
    static final MessageConverter CUSTOM_CONVERTER =
        new Jackson2JsonMessageConverter(new ObjectMapper(), "*");

    @Bean
    public MessageConverter messageConverter() {
      return CUSTOM_CONVERTER;
    }
  }
}
