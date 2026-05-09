package top.ppmblszdp.gateway.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.common.redis.util.RedisUtil;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;
import top.ppmblszdp.gateway.config.RateLimitProperties;

/**
 * 动态限流过滤器.
 *
 * <p>根据用户 ID 或客户端 IP 进行限流。
 */
@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private final RedisRateLimiter redisRateLimiter;
  private final JwtUtils jwtUtils;
  private final PpmbSecurityProperties securityProperties;
  private final RateLimitProperties rateLimitProperties;
  private final RedisUtil redisUtil;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!rateLimitProperties.enabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String routeId = resolveRouteId(request);
    RateLimitIdentity identity = resolveIdentity(request);
    String rateLimitKey = "rate_limit:" + routeId + ":" + identity.type() + ":" + identity.value();
    RateLimitRule rule = resolveRule(routeId, identity);

    boolean allowed =
        redisRateLimiter.isAllowed(rateLimitKey, rule.count(), rule.period());

    if (!allowed) {
      log.warn("Rate limit exceeded for key: {}", rateLimitKey);
      writeErrorResponse(request, response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void writeErrorResponse(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
    problemDetail.setTitle("Too Many Requests");
    problemDetail.setType(URI.create("https://ppmb.top/errors/too-many-requests"));
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("code", "A0501");

    objectMapper.writeValue(response.getWriter(), problemDetail);
  }

  private RateLimitIdentity resolveIdentity(HttpServletRequest request) {
    String headerName = securityProperties.getJwt().getHeaderName();
    String headerValue = request.getHeader(headerName);

    return jwtUtils
        .extractToken(headerValue)
        .filter(jwtUtils::validateToken)
        .flatMap(
            token -> {
              try {
                Claims claims = jwtUtils.parseToken(token);
                return Optional.ofNullable(claims.getSubject()).filter(s -> !s.isEmpty());
              } catch (Exception _) {
                log.debug("Failed to parse token for rate limiting");
                return Optional.empty();
              }
            })
        .map(subject -> new RateLimitIdentity("user", subject))
        .orElseGet(() -> new RateLimitIdentity("ip", getClientIp(request)));
  }

  private String resolveRouteId(HttpServletRequest request) {
    Object routeIdAttr = request.getAttribute(MvcUtils.GATEWAY_ROUTE_ID_ATTR);
    if (routeIdAttr == null) {
      return "default";
    }
    String routeId = routeIdAttr.toString();
    return StringUtils.hasText(routeId) ? routeId : "default";
  }

  private RateLimitRule resolveRule(String routeId, RateLimitIdentity identity) {
    String prefix = rateLimitProperties.ruleKeyPrefix();
    List<String> candidateKeys =
        List.of(
            prefix + ":route:" + routeId + ":" + identity.type() + ":" + identity.value(),
            prefix + ":" + identity.type() + ":" + identity.value(),
            prefix + ":route:" + routeId,
            prefix + ":default");

    for (String key : candidateKeys) {
      String value = redisUtil.get(key, String.class).orElse(null);
      RateLimitRule parsedRule = RateLimitRule.parse(value);
      if (parsedRule != null) {
        return parsedRule;
      }
    }

    return new RateLimitRule(rateLimitProperties.count(), rateLimitProperties.period());
  }

  private String getClientIp(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
        .filter(h -> !h.isBlank() && !"unknown".equalsIgnoreCase(h))
        .map(h -> h.split(",")[0].trim())
        .or(
            () ->
                Optional.ofNullable(request.getHeader("X-Real-IP"))
                    .filter(h -> !h.isBlank() && !"unknown".equalsIgnoreCase(h)))
        .orElseGet(request::getRemoteAddr);
  }

  private record RateLimitIdentity(String type, String value) {}

  private record RateLimitRule(int count, int period) {
    private static RateLimitRule parse(String value) {
      if (!StringUtils.hasText(value)) {
        return null;
      }

      String[] parts = value.trim().split("[:,]");
      if (parts.length != 2) {
        return null;
      }

      try {
        int count = Integer.parseInt(parts[0].trim());
        int period = Integer.parseInt(parts[1].trim());
        if (count <= 0 || period <= 0) {
          return null;
        }
        return new RateLimitRule(count, period);
      } catch (NumberFormatException _) {
        return null;
      }
    }
  }
}
