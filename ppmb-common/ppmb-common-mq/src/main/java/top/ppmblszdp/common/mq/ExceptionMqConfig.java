package top.ppmblszdp.common.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ppmblszdp.common.api.constant.MqConstants;

/** 异常日志队列配置. */
@Configuration
public class ExceptionMqConfig {

  @Bean
  public DirectExchange exceptionExchange() {
    return new DirectExchange(MqConstants.EXCEPTION_EXCHANGE, true, false);
  }

  @Bean
  public Queue exceptionQueue() {
    return new Queue(MqConstants.EXCEPTION_QUEUE, true);
  }

  @Bean
  public Binding exceptionBinding(Queue exceptionQueue, DirectExchange exceptionExchange) {
    return BindingBuilder.bind(exceptionQueue)
        .to(exceptionExchange)
        .with(MqConstants.EXCEPTION_ROUTING_KEY);
  }
}
