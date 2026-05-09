package top.ppmblszdp.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalNameKeyResolverTest {

  private PrincipalNameKeyResolver resolver;

  @BeforeEach
  public void setUp() {
    resolver = new PrincipalNameKeyResolver();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void testResolveFromSecurityContext() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            "user-sec", "N/A", AuthorityUtils.createAuthorityList("ROLE_USER"));
    SecurityContextHolder.getContext().setAuthentication(auth);

    String key = resolver.resolve(request);
    assertEquals("user-sec", key);
  }

  @Test
  public void testResolveFromSecurityContextWithNull() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");
    SecurityContextHolder.getContext().setAuthentication(null);

    String key = resolver.resolve(request);
    assertEquals("192.168.1.1", key);
  }

  @Test
  public void testResolveFromHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-User-Id")).thenReturn("user-head");

    String key = resolver.resolve(request);
    assertEquals("user-head", key);
  }

  @Test
  public void testResolveFromIp() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");

    String key = resolver.resolve(request);
    assertEquals("192.168.1.1", key);
  }

  @Test
  public void testResolveFromForwardedIp() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");
    when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2");

    String key = resolver.resolve(request);
    assertEquals("10.0.0.1", key);
  }

  @Test
  public void testResolveUnknown() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    String key = resolver.resolve(request);
    assertEquals("unknown-ip", key);
  }
}
