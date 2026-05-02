package top.ppmblszdp.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;

@DisplayName("Header 认证过滤器测试")
class HeaderAuthenticationFilterTest {

  private HeaderAuthenticationFilter filter;
  private PpmbSecurityProperties properties;

  @BeforeEach
  void setUp() {
    properties = new PpmbSecurityProperties();
    filter = new HeaderAuthenticationFilter(properties);
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("网关模式下应跳过过滤器")
  void skipInGatewayMode() throws Exception {
    properties.setGatewayMode(true);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("解析 Header 中的用户信息并设置 SecurityContext")
  void authenticateFromHeaders() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(properties.getHeader().getUserId())).thenReturn("123");
    when(request.getHeader(properties.getHeader().getUsername())).thenReturn("testuser");
    when(request.getHeader(properties.getHeader().getRoles())).thenReturn("ADMIN,USER");

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("testuser", auth.getName());
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("没有用户 ID Header 时不设置 Authentication")
  void noUserIdHeader() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("请求头缺失时不应设置 Authentication")
  void authenticateWithMissingHeaders() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(anyString())).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("缺失用户名时应使用用户 ID 作为 Principal，且处理空角色头")
  void authenticateWithMissingUsernameAndEmptyRoles() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(properties.getHeader().getUserId())).thenReturn("123");
    when(request.getHeader(properties.getHeader().getUsername())).thenReturn("");
    when(request.getHeader(properties.getHeader().getRoles())).thenReturn("  ");

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("123", auth.getName());
    assertTrue(auth.getAuthorities().isEmpty());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("解析带有 ROLE_ 前缀的角色 Header")
  void authenticateFromHeadersWithPrefixedRoles() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(properties.getHeader().getUserId())).thenReturn("123");
    when(request.getHeader(properties.getHeader().getUsername())).thenReturn("testuser");
    // 一个带前缀，一个不带
    when(request.getHeader(properties.getHeader().getRoles())).thenReturn("ROLE_ADMIN,USER");

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }
}
