package top.ppmblszdp.gateway.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 自定义限流 Key 解析器：用户 ID 优先，IP 降级. 由于网关集成了 MVC 和 Spring Security，可以通过 SecurityContext 或 Header 获取用户
 * ID。
 */
@Component("userKeyResolver")
public class PrincipalNameKeyResolver {

  public String resolve(HttpServletRequest request) {
    // 1. 尝试从 SecurityContext 获取
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && !"anonymousUser".equals(authentication.getPrincipal())) {
      return authentication.getName();
    }

    // 2. 尝试从请求头 X-User-Id 获取
    String userId = request.getHeader("X-User-Id");
    if (StringUtils.hasText(userId)) {
      return userId;
    }

    // 3. 降级使用 IP
    String ip = request.getRemoteAddr();
    // 简单获取远端 IP。如果在代理后，可能需要根据 X-Forwarded-For 获取真实 IP，这里简化处理。
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (StringUtils.hasText(forwardedFor)) {
      ip = forwardedFor.split(",")[0].trim();
    }

    return StringUtils.hasText(ip) ? ip : "unknown-ip";
  }
}
