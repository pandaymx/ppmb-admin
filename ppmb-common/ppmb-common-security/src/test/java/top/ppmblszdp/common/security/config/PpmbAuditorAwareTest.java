package top.ppmblszdp.common.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("PpmbAuditorAware 单元测试")
class PpmbAuditorAwareTest {

  private final PpmbAuditorAware auditorAware = new PpmbAuditorAware();

  @Test
  @DisplayName("应该从请求头获取用户 ID")
  void shouldGetUserIdFromHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-User-Id", "123");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    try {
      Optional<Long> auditor = auditorAware.getCurrentAuditor();
      assertThat(auditor).isPresent().contains(123L);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  @DisplayName("没有请求头时应该返回默认用户 ID")
  void shouldReturnDefaultUserIdWhenNoHeader() {
    RequestContextHolder.resetRequestAttributes();
    Optional<Long> auditor = auditorAware.getCurrentAuditor();
    assertThat(auditor).isPresent().contains(1L);
  }
}
