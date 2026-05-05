package top.ppmblszdp.system.infrastructure.mq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.system.domain.model.log.entity.SysExceptionLog;
import top.ppmblszdp.system.domain.model.log.repository.ExceptionLogRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("异常日志 MQ 监听器测试")
class ExceptionLogListenerTest {

  @Mock private ExceptionLogRepository exceptionLogRepository;

  private ExceptionLogListener exceptionLogListener;

  @BeforeEach
  void setUp() {
    exceptionLogListener = new ExceptionLogListener(exceptionLogRepository);
  }

  @Test
  @DisplayName("接收到消息时应正确保存到数据库")
  void testHandleExceptionLog() {
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "test-service",
            "java.lang.RuntimeException",
            "test error",
            "stack trace",
            "/test",
            "GET",
            "id=1",
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    exceptionLogListener.handleExceptionLog(message);

    verify(exceptionLogRepository, times(1)).save(any(SysExceptionLog.class));
  }

  @Test
  @DisplayName("保存失败时不应抛出异常")
  void testHandleExceptionLog_SaveError() {
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "test-service",
            "java.lang.RuntimeException",
            "test error",
            "stack trace",
            "/test",
            "GET",
            "id=1",
            "127.0.0.1",
            1L,
            LocalDateTime.now());

    org.mockito.Mockito.when(exceptionLogRepository.save(any()))
        .thenThrow(new RuntimeException("DB error"));

    // 不应抛出异常
    exceptionLogListener.handleExceptionLog(message);

    verify(exceptionLogRepository, times(1)).save(any(SysExceptionLog.class));
  }
}
