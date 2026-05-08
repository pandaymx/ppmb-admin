package top.ppmblszdp.system.infrastructure.mq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.system.domain.model.log.entity.SysAuditLog;
import top.ppmblszdp.system.domain.model.log.repository.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("审计日志 MQ 监听器测试")
class AuditLogListenerTest {

  @Mock private AuditLogRepository auditLogRepository;

  private AuditLogListener auditLogListener;

  @BeforeEach
  void setUp() {
    auditLogListener = new AuditLogListener(auditLogRepository);
  }

  @Test
  @DisplayName("接收到新消息时应正确保存到数据库")
  void testOnAuditLogMessage_New() {
    String traceId = UUID.randomUUID().toString();
    AuditLogMessage message =
        new AuditLogMessage(
            traceId,
            "INSERT",
            "User",
            "1",
            null,
            "{}",
            "/users",
            "POST",
            "{}",
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    when(auditLogRepository.existsByTraceId(traceId)).thenReturn(false);

    auditLogListener.onAuditLogMessage(message);

    verify(auditLogRepository, times(1)).save(any(SysAuditLog.class));
  }

  @Test
  @DisplayName("接收到重复消息时应忽略")
  void testOnAuditLogMessage_Duplicate() {
    String traceId = UUID.randomUUID().toString();
    AuditLogMessage message =
        new AuditLogMessage(
            traceId,
            "INSERT",
            "User",
            "1",
            null,
            "{}",
            "/users",
            "POST",
            "{}",
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    when(auditLogRepository.existsByTraceId(traceId)).thenReturn(true);

    auditLogListener.onAuditLogMessage(message);

    verify(auditLogRepository, never()).save(any(SysAuditLog.class));
  }
}
