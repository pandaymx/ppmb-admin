package top.ppmblszdp.common.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExceptionLogMessage 单元测试")
class ExceptionLogMessageTest {

  @Test
  @DisplayName("测试 Record 构造与属性访问")
  void testRecord() {
    LocalDateTime now = LocalDateTime.now();
    ExceptionLogMessage message =
        new ExceptionLogMessage(
            "ppmb-system",
            "java.lang.NullPointerException",
            "npe message",
            "stack trace...",
            "/users/1",
            "GET",
            "id=1",
            "127.0.0.1",
            1L,
            now);

    assertThat(message.serviceName()).isEqualTo("ppmb-system");
    assertThat(message.exceptionName()).isEqualTo("java.lang.NullPointerException");
    assertThat(message.message()).isEqualTo("npe message");
    assertThat(message.stackTrace()).isEqualTo("stack trace...");
    assertThat(message.requestUri()).isEqualTo("/users/1");
    assertThat(message.requestMethod()).isEqualTo("GET");
    assertThat(message.requestParams()).isEqualTo("id=1");
    assertThat(message.ip()).isEqualTo("127.0.0.1");
    assertThat(message.userId()).isEqualTo(1L);
    assertThat(message.createTime()).isEqualTo(now);
  }
}
