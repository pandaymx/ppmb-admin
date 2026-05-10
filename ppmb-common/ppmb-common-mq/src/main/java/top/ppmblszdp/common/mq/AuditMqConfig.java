package top.ppmblszdp.common.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ppmblszdp.common.api.constant.MqConstants;

/** 审计日志队列配置. */
@Configuration
public class AuditMqConfig {

  @Bean
  public DirectExchange auditExchange() {
    return new DirectExchange(MqConstants.AUDIT_LOG_EXCHANGE, true, false);
  }

  @Bean
  public Queue auditQueue() {
    return new Queue(MqConstants.AUDIT_LOG_QUEUE, true);
  }

  @Bean
  public Binding auditBinding(
      @Qualifier("auditQueue") Queue auditQueue,
      @Qualifier("auditExchange") DirectExchange auditExchange) {
    return BindingBuilder.bind(auditQueue)
        .to(auditExchange)
        .with(MqConstants.AUDIT_LOG_ROUTING_KEY);
  }
}
