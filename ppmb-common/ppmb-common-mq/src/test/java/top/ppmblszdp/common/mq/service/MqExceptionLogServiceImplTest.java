package top.ppmblszdp.common.mq.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("MQ异常日志记录服务测试")
class MqExceptionLogServiceImplTest {

  @Mock private RabbitTemplate rabbitTemplate;

  private MqExceptionLogServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new MqExceptionLogServiceImpl(rabbitTemplate);
  }

  @Test
  @DisplayName("发送日志时应调用 RabbitTemplate")
  void testSend() {
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "test-service",
            "Exception",
            "msg",
            "stack",
            "/uri",
            "GET",
            null,
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    service.send(message);

    verify(rabbitTemplate)
        .convertAndSend(
            eq(MqConstants.EXCEPTION_EXCHANGE), eq(MqConstants.EXCEPTION_ROUTING_KEY), eq(message));
  }

  @Test
  @DisplayName("当 RabbitTemplate 抛出异常时应捕获并记录日志")
  void testSendWithException() {
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "test-service",
            "Exception",
            "msg",
            "stack",
            "/uri",
            "GET",
            null,
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    doThrow(new RuntimeException("MQ Error"))
        .when(rabbitTemplate)
        .convertAndSend(any(String.class), any(String.class), any(Object.class));

    // Should not throw exception
    service.send(message);

    verify(rabbitTemplate)
        .convertAndSend(
            eq(MqConstants.EXCEPTION_EXCHANGE), eq(MqConstants.EXCEPTION_ROUTING_KEY), eq(message));
  }
}
