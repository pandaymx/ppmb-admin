package top.ppmblszdp.common.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExceptionLogMessage 单元测试")
class ExceptionLogMessageTest {

  @Test
  @DisplayName("record 属性应能正确初始化和读取")
  void shouldInitializeCorrectly() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "test-service",
            "NullPointerException",
            "Test message",
            "Stack trace",
            "/api/test",
            "GET",
            "{}",
            "127.0.0.1",
            1L,
            now);

    // Act & Assert
    assertEquals("test-service", message.serviceName());
    assertEquals("NullPointerException", message.exceptionName());
    assertEquals("Test message", message.message());
    assertEquals("Stack trace", message.stackTrace());
    assertEquals("/api/test", message.requestUri());
    assertEquals("GET", message.requestMethod());
    assertEquals("{}", message.requestParams());
    assertEquals("127.0.0.1", message.ip());
    assertEquals(1L, message.userId());
    assertEquals(now, message.createTime());
    assertNotNull(message.toString());
  }
}
