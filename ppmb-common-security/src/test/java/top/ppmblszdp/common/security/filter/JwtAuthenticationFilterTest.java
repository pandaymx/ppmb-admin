package top.ppmblszdp.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

@DisplayName("JWT 认证过滤器测试")
class JwtAuthenticationFilterTest {

  private JwtAuthenticationFilter filter;
  private PpmbSecurityProperties properties;
  private JwtUtils jwtUtils;

  @BeforeEach
  void setUp() {
    properties = new PpmbSecurityProperties();
    jwtUtils = mock(JwtUtils.class);
    filter = new JwtAuthenticationFilter(properties, jwtUtils);
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("非网关模式下应跳过过滤器")
  void skipInNonGatewayMode() throws Exception {
    properties.setGatewayMode(false);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("解析有效的 JWT 并设置 SecurityContext")
  void authenticateWithValidJwt() throws Exception {
    properties.setGatewayMode(true);
    HttpServletRequest request = mock(HttpServletRequest.class);
    String token = "valid-token";
    String headerValue = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(headerValue);
    when(jwtUtils.extractToken(headerValue)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(true);

    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("testuser");
    when(claims.get("roles", String.class)).thenReturn("ADMIN");
    when(jwtUtils.parseToken(token)).thenReturn(claims);

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("testuser", auth.getName());
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("无效的 JWT 不应设置 Authentication")
  void invalidJwt() throws Exception {
    properties.setGatewayMode(true);
    HttpServletRequest request = mock(HttpServletRequest.class);
    String token = "invalid-token";
    String headerValue = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(headerValue);
    when(jwtUtils.extractToken(headerValue)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(false);

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
