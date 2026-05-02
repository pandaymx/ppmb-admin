package top.ppmblszdp.common.mq;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import tools.jackson.databind.json.JsonMapper;

/**
 * RabbitMQ AutoConfiguration for ppmb-common-mq. Provides generic JSON serialization and Retry
 * configuration.
 */
@AutoConfiguration
@ConditionalOnClass(org.springframework.amqp.rabbit.core.RabbitTemplate.class)
public class RabbitMqConfig {

  /**
   * Configures JacksonJsonMessageConverter to ensure message payloads are passed as JSON. This
   * avoids native Java serialization which can cause class compatibility issues across
   * microservices.
   *
   * @return the MessageConverter instance
   */
  @Bean
  @ConditionalOnMissingBean(MessageConverter.class)
  public MessageConverter jsonMessageConverter() {
    JsonMapper jsonMapper = JsonMapper.builder().build();
    return new JacksonJsonMessageConverter(jsonMapper, "*");
  }

  /**
   * Provides a generic RetryTemplate. Useful for recovering from transient network issues when
   * interacting with the message broker.
   *
   * @return the RetryTemplate instance
   */
  @Bean
  @ConditionalOnMissingBean(name = "rabbitRetryTemplate")
  public RetryTemplate rabbitRetryTemplate() {
    return RetryTemplate.builder().maxAttempts(3).exponentialBackoff(1000L, 2.0, 10000L).build();
  }
}
