package top.ppmblszdp.common.mq;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ExceptionMqConfig.class)
@DisplayName("异常 MQ 配置测试")
class ExceptionMqConfigTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  @DisplayName("应该正确创建异常交换机、队列和绑定")
  void testBeansCreated() {
    assertNotNull(applicationContext.getBean(DirectExchange.class), "应创建 DirectExchange 实例");
    assertNotNull(applicationContext.getBean(Queue.class), "应创建 Queue 实例");
    assertNotNull(applicationContext.getBean(Binding.class), "应创建 Binding 实例");
  }
}
