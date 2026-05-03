package top.ppmblszdp.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;

/**
 * Filter for downstream services. Reads user information from headers passed by the gateway and
 * populates the SecurityContext.
 */
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

  private final PpmbSecurityProperties properties;

  public HeaderAuthenticationFilter(PpmbSecurityProperties properties) {
    this.properties = properties;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // If gateway mode is true, we should skip this filter, although it shouldn't be registered in
    // that case.
    if (properties.isGatewayMode()) {
      filterChain.doFilter(request, response);
      return;
    }

    String userIdHeader = request.getHeader(properties.getHeader().getUserId());
    String usernameHeader = request.getHeader(properties.getHeader().getUsername());
    String rolesHeader = request.getHeader(properties.getHeader().getRoles());

    if (StringUtils.hasText(userIdHeader)) {
      List<GrantedAuthority> authorities = new ArrayList<>();
      if (StringUtils.hasText(rolesHeader)) {
        authorities =
            Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(
                    role ->
                        (GrantedAuthority)
                            new SimpleGrantedAuthority(
                                role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();
      }

      // In a real application, you might create a custom UserDetails object here.
      // For now, using the username (or user ID) as the principal.
      String principal = StringUtils.hasText(usernameHeader) ? usernameHeader : userIdHeader;
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(principal, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
