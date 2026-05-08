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

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.MethodSource("provideDefaultUserIdScenarios")
  @DisplayName("各种无法获取用户 ID 的场景应返回默认值 1")
  void shouldReturnDefaultUserIdForVariousScenarios(
      org.springframework.security.core.context.SecurityContext context,
      HttpServletRequest request) {
    if (context != null) {
      SecurityContextHolder.setContext(context);
    }
    if (request != null) {
      RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
    assertEquals(1L, SecurityUtils.getUserId());
  }

  static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments>
      provideDefaultUserIdScenarios() {
    // 1. Authentication is null
    SecurityContext ctx1 = mock(SecurityContext.class);
    when(ctx1.getAuthentication()).thenReturn(null);

    // 2. Principal is null
    Authentication auth2 = mock(Authentication.class);
    when(auth2.getPrincipal()).thenReturn(null);
    SecurityContext ctx2 = mock(SecurityContext.class);
    when(ctx2.getAuthentication()).thenReturn(auth2);

    // 3. Principal is not a number string
    Authentication auth3 = mock(Authentication.class);
    when(auth3.getPrincipal()).thenReturn("not-a-number");
    SecurityContext ctx3 = mock(SecurityContext.class);
    when(ctx3.getAuthentication()).thenReturn(auth3);

    // 4. Principal is not a string
    Authentication auth4 = mock(Authentication.class);
    when(auth4.getPrincipal()).thenReturn(123);
    SecurityContext ctx4 = mock(SecurityContext.class);
    when(ctx4.getAuthentication()).thenReturn(auth4);

    // 5. Header is null
    HttpServletRequest req5 = mock(HttpServletRequest.class);
    when(req5.getHeader("X-User-Id")).thenReturn(null);

    // 6. Header is empty
    HttpServletRequest req6 = mock(HttpServletRequest.class);
    when(req6.getHeader("X-User-Id")).thenReturn("");

    return java.util.stream.Stream.of(
        org.junit.jupiter.params.provider.Arguments.of(ctx1, null),
        org.junit.jupiter.params.provider.Arguments.of(ctx2, null),
        org.junit.jupiter.params.provider.Arguments.of(ctx3, null),
        org.junit.jupiter.params.provider.Arguments.of(ctx4, null),
        org.junit.jupiter.params.provider.Arguments.of(null, req5),
        org.junit.jupiter.params.provider.Arguments.of(null, req6),
        org.junit.jupiter.params.provider.Arguments.of(null, null) // Default case
        );
  }

  @Test
  @DisplayName("测试私有构造函数")
  void testPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<SecurityUtils> constructor =
        SecurityUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertEquals(
          "This is a utility class and cannot be instantiated",
          e.getTargetException().getMessage());
    }
  }
}
