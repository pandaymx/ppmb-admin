package top.ppmblszdp.common.mq.listener;

import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.mq.service.AuditLogService;

@ExtendWith(MockitoExtension.class)
@DisplayName("审计应用事件监听器测试")
class AuditApplicationEventListenerTest {

  @Mock private AuditLogService auditLogService;

  private AuditApplicationEventListener listener;

  @BeforeEach
  void setUp() {
    listener = new AuditApplicationEventListener(auditLogService);
  }

  @Test
  @DisplayName("接收到审计日志事件时应调用 MQ 服务发送")
  void testHandleAuditLogEvent() {
    AuditLogMessage message = new AuditLogMessage(
        UUID.randomUUID().toString(), "INSERT", "User", "1", null, "{}", "/users", "POST", "{}", "127.0.0.1", 1L, LocalDateTime.now());
    AuditLogEvent event = new AuditLogEvent(this, message);

    listener.handleAuditLogEvent(event);

    verify(auditLogService).send(message);
  }
}
