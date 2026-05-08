package top.ppmblszdp.common.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

/** Filter for gateway or standalone services. Parses JWT and populates SecurityContext. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final PpmbSecurityProperties properties;
  private final JwtUtils jwtUtils;

  public JwtAuthenticationFilter(PpmbSecurityProperties properties, JwtUtils jwtUtils) {
    this.properties = properties;
    this.jwtUtils = jwtUtils;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // If gateway mode is false, this filter shouldn't do anything as we rely on headers
    if (!properties.isGatewayMode()) {
      filterChain.doFilter(request, response);
      return;
    }

    String headerName = properties.getJwt().getHeaderName();
    String headerValue = request.getHeader(headerName);

    Optional<String> tokenOpt = jwtUtils.extractToken(headerValue);
    if (tokenOpt.isPresent() && jwtUtils.validateToken(tokenOpt.get())) {
      Claims claims = jwtUtils.parseToken(tokenOpt.get());
      String subject = claims.getSubject();

      // Assuming roles are stored in a "roles" claim as comma separated string
      String rolesStr = claims.get("roles", String.class);
      List<GrantedAuthority> authorities = new ArrayList<>();

      if (StringUtils.hasText(rolesStr)) {
        authorities =
            Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(
                    role ->
                        (GrantedAuthority)
                            new SimpleGrantedAuthority(
                                role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();
      }

      if (StringUtils.hasText(subject)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(subject, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      // Extract tenantId from JWT claims and inject it into the request header for downstream filters (or routing)
      Long tenantId = claims.get("tenantId", Long.class);
      if (tenantId == null) {
          Number tidNum = claims.get("tid", Number.class);
          if (tidNum != null) {
              tenantId = tidNum.longValue();
          }
      }

      if (tenantId != null) {
          HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
          requestWrapper.addHeader(properties.getHeader().getTenantId(), String.valueOf(tenantId));
          requestWrapper.addHeader(properties.getHeader().getUserId(), subject);
          if (StringUtils.hasText(rolesStr)) {
              requestWrapper.addHeader(properties.getHeader().getRoles(), rolesStr);
          }
          filterChain.doFilter(requestWrapper, response);
          return;
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Wrapper to allow modifying HTTP headers.
   */
  public static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
      private final Map<String, String> headerMap = new HashMap<>();

      public HeaderMapRequestWrapper(HttpServletRequest request) {
          super(request);
      }

      public void addHeader(String name, String value) {
          headerMap.put(name, value);
      }

      @Override
      public String getHeader(String name) {
          String headerValue = super.getHeader(name);
          if (headerMap.containsKey(name)) {
              headerValue = headerMap.get(name);
          }
          return headerValue;
      }

      @Override
      public Enumeration<String> getHeaderNames() {
          List<String> names = Collections.list(super.getHeaderNames());
          names.addAll(headerMap.keySet());
          return Collections.enumeration(names);
      }

      @Override
      public Enumeration<String> getHeaders(String name) {
          List<String> values = Collections.list(super.getHeaders(name));
          if (headerMap.containsKey(name)) {
              values.add(headerMap.get(name));
          }
          return Collections.enumeration(values);
      }
  }
}
