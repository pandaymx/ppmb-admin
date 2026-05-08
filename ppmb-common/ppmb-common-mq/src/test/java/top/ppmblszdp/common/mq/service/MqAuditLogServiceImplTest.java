package top.ppmblszdp.common.mq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.api.service.ExceptionLogService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MQ审计日志服务测试")
class MqAuditLogServiceImplTest {

  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private ExceptionLogService exceptionLogService;

  private MqAuditLogServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new MqAuditLogServiceImpl(rabbitTemplate, exceptionLogService);
  }

  @Test
  @DisplayName("发送审计日志成功时应调用 RabbitTemplate")
  void shouldSendAuditLogToMq() {
    AuditLogMessage message =
        new AuditLogMessage(
            "trace-id",
            "UPDATE",
            "User",
            "1",
            "{}",
            "{\"name\":\"new\"}",
            "/users/1",
            "PUT",
            "{\"name\":\"new\"}",
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    service.send(message);

    verify(rabbitTemplate)
        .convertAndSend(MqConstants.AUDIT_LOG_EXCHANGE, MqConstants.AUDIT_LOG_ROUTING_KEY, message);
  }

  @Test
  @DisplayName("发送失败时应回退到异常日志服务")
  void shouldFallbackToExceptionLogWhenMqSendFails() {
    AuditLogMessage message =
        new AuditLogMessage(
            "trace-id",
            "INSERT",
            "Order",
            "2",
            null,
            "{\"id\":2}",
            "/orders",
            "POST",
            "{\"id\":2}",
            "10.0.0.1",
            2L,
            LocalDateTime.now());
    RuntimeException exception = new RuntimeException("MQ down");
    doThrow(exception)
        .when(rabbitTemplate)
        .convertAndSend(any(String.class), any(String.class), any(Object.class));

    service.send(message);

    verify(rabbitTemplate)
        .convertAndSend(MqConstants.AUDIT_LOG_EXCHANGE, MqConstants.AUDIT_LOG_ROUTING_KEY, message);
    ArgumentCaptor<ExceptionLogMessage> captor = ArgumentCaptor.forClass(ExceptionLogMessage.class);
    verify(exceptionLogService).send(captor.capture());

    ExceptionLogMessage fallbackMessage = captor.getValue();
    assertNotNull(fallbackMessage);
    assertEquals("ppmb-common-mq", fallbackMessage.serviceName());
    assertEquals(RuntimeException.class.getName(), fallbackMessage.exceptionName());
    assertEquals("Failed to send audit log: MQ down", fallbackMessage.message());
    assertEquals("/orders", fallbackMessage.requestUri());
    assertEquals("POST", fallbackMessage.requestMethod());
    assertEquals("{\"id\":2}", fallbackMessage.requestParams());
    assertEquals("10.0.0.1", fallbackMessage.ip());
    assertEquals(2L, fallbackMessage.userId());
    assertNotNull(fallbackMessage.createTime());
  }
}
