package top.ppmblszdp.common.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("SecurityUtils 工具类测试")
class SecurityUtilsTest {

  @BeforeEach
  void setUp() {
    clearContext();
  }

  @AfterEach
  void tearDown() {
    clearContext();
  }

  private void clearContext() {
    RequestContextHolder.resetRequestAttributes();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("从请求头获取用户 ID")
  void shouldGetUserIdFromHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-User-Id")).thenReturn("123");
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    assertEquals(123L, SecurityUtils.getUserId());
  }

  @Test
  @DisplayName("从 SecurityContext 获取用户 ID")
  void shouldGetUserIdFromSecurityContext() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("456");
    when(authentication.isAuthenticated()).thenReturn(true);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    assertEquals(456L, SecurityUtils.getUserId());
  }

  @Test
  @DisplayName("无法获取时返回默认用户 ID 1")
  void shouldReturnDefaultUserIdWhenNotFound() {
    assertEquals(1L, SecurityUtils.getUserId());
  }

  @Test
  @DisplayName("Principal 非数字时返回默认用户 ID 1")
  void shouldReturnDefaultUserIdWhenPrincipalNotNumber() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("not-a-number");

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    assertEquals(1L, SecurityUtils.getUserId());
  }
}
