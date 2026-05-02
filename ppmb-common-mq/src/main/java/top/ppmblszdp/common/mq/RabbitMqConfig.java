package top.ppmblszdp.common.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * RabbitMQ AutoConfiguration for ppmb-common-mq. Provides generic JSON serialization and Retry
 * configuration.
 */
@AutoConfiguration
@ConditionalOnClass(org.springframework.amqp.rabbit.core.RabbitTemplate.class)
public class RabbitMqConfig {

  /**
   * Configures Jackson2JsonMessageConverter to ensure message payloads are passed as JSON. This
   * avoids native Java serialization which can cause class compatibility issues across
   * microservices.
   *
   * @param objectMapper Spring's default ObjectMapper
   * @return the MessageConverter instance
   */
  @Bean
  @ConditionalOnMissingBean(MessageConverter.class)
  @SuppressWarnings("deprecation")
  public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
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
    RetryTemplate retryTemplate = new RetryTemplate();

    // Exponential backoff strategy: Start with 1s, double up to 10s
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(1000L);
    backOffPolicy.setMultiplier(2.0);
    backOffPolicy.setMaxInterval(10000L);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    // Max 3 retry attempts
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }
}
