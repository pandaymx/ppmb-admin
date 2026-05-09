package top.ppmblszdp.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.exception.ProblemDetailAuthenticationEntryPoint;
import top.ppmblszdp.common.tenant.TenantContextHolder;

/** Filter to extract tenant ID from HTTP headers and store it in TenantContextHolder. */
public class TenantAuthenticationFilter extends OncePerRequestFilter {

  private final PpmbSecurityProperties properties;
  private final ProblemDetailAuthenticationEntryPoint authenticationEntryPoint;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  public TenantAuthenticationFilter(
      PpmbSecurityProperties properties,
      ProblemDetailAuthenticationEntryPoint authenticationEntryPoint) {
    this.properties = properties;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();

    // Skip if it's an ignored URL
    if (properties.getIgnoreUrls() != null
        && properties.getIgnoreUrls().stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestUri))) {
      filterChain.doFilter(request, response);
      return;
    }

    // Also skip actuator
    if (requestUri.startsWith("/actuator")) {
      filterChain.doFilter(request, response);
      return;
    }

    String tenantIdHeader = request.getHeader(properties.getHeader().getTenantId());

    try {
      if (StringUtils.hasText(tenantIdHeader)) {
        try {
          Long tenantId = Long.parseLong(tenantIdHeader);
          TenantContextHolder.set(tenantId);
          filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
          throw new TenantMissingException("Invalid Tenant ID format in header");
        }
      } else {
        throw new TenantMissingException("Tenant ID is missing in header");
      }
    } catch (AuthenticationException e) {
      authenticationEntryPoint.commence(request, response, e);
    } finally {
      TenantContextHolder.clear();
    }
  }

  /** Custom exception for missing or invalid tenant. */
  public static class TenantMissingException extends AuthenticationException {
    public TenantMissingException(String msg) {
      super(msg);
    }
  }
}
