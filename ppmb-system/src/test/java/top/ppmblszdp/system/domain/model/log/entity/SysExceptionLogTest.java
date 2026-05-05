package top.ppmblszdp.system.domain.model.log.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("异常日志实体单元测试")
class SysExceptionLogTest {

  @Test
  @DisplayName("prePersist 应该初始化创建时间")
  void prePersistInitializesCreateTime() {
    SysExceptionLog log = new SysExceptionLog();
    assertThat(log.getCreateTime()).isNull();

    log.prePersist();

    assertThat(log.getCreateTime()).isNotNull();
  }

  @Test
  @DisplayName("prePersist 不应该覆盖已有的 ID 和创建时间")
  void prePersistDoesNotOverwrite() {
    LocalDateTime customTime = LocalDateTime.of(2025, 1, 1, 12, 0);
    SysExceptionLog log = new SysExceptionLog();
    log.setId(12345L);
    log.setCreateTime(customTime);

    log.prePersist();

    assertThat(log.getId()).isEqualTo(12345L);
    assertThat(log.getCreateTime()).isEqualTo(customTime);
  }

  @Test
  @DisplayName("验证 Getter 和 Setter")
  void testGettersAndSetters() {
    SysExceptionLog log = new SysExceptionLog();
    log.setServiceName("test-service");
    log.setExceptionName("RuntimeException");
    log.setMessage("error message");
    log.setStackTrace("trace");
    log.setRequestUri("/api/test");
    log.setRequestMethod("POST");
    log.setRequestParams("id=1");
    log.setIp("127.0.0.1");
    log.setUserId(1L);

    assertThat(log.getServiceName()).isEqualTo("test-service");
    assertThat(log.getExceptionName()).isEqualTo("RuntimeException");
    assertThat(log.getMessage()).isEqualTo("error message");
    assertThat(log.getStackTrace()).isEqualTo("trace");
    assertThat(log.getRequestUri()).isEqualTo("/api/test");
    assertThat(log.getRequestMethod()).isEqualTo("POST");
    assertThat(log.getRequestParams()).isEqualTo("id=1");
    assertThat(log.getIp()).isEqualTo("127.0.0.1");
    assertThat(log.getUserId()).isEqualTo(1L);
    assertThat(log.toString()).contains("serviceName=test-service");
  }
}
