package top.ppmblszdp.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private final RedisRateLimiter redisRateLimiter;
  private final JwtUtils jwtUtils;
  private final PpmbSecurityProperties securityProperties;
  private final ObjectMapper objectMapper;

  @Value("${ppmb.gateway.rate-limit.enabled:true}")
  private boolean rateLimitEnabled;

  @Value("${ppmb.gateway.rate-limit.count:10}")
  private int rateLimitCount;

  @Value("${ppmb.gateway.rate-limit.period:1}")
  private int rateLimitPeriod;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!rateLimitEnabled) {
      filterChain.doFilter(request, response);
      return;
    }

    String rateLimitKey = resolveRateLimitKey(request);

    boolean allowed = redisRateLimiter.isAllowed(rateLimitKey, rateLimitCount, rateLimitPeriod);

    if (!allowed) {
      log.warn("Rate limit exceeded for key: {}", rateLimitKey);
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");

      Result<Void> result = Result.failure(CommonResultCode.USER_ERROR.getCode(), "请求过于频繁，请稍后再试");
      response.getWriter().write(objectMapper.writeValueAsString(result));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String resolveRateLimitKey(HttpServletRequest request) {
    String headerName = securityProperties.getJwt().getHeaderName();
    String headerValue = request.getHeader(headerName);

    Optional<String> tokenOpt = jwtUtils.extractToken(headerValue);
    if (tokenOpt.isPresent() && jwtUtils.validateToken(tokenOpt.get())) {
      try {
        Claims claims = jwtUtils.parseToken(tokenOpt.get());
        String subject = claims.getSubject();
        if (subject != null && !subject.isEmpty()) {
          return "rate_limit:user:" + subject;
        }
      } catch (Exception e) {
        log.debug("Failed to parse token for rate limiting", e);
      }
    }

    // Fallback to IP address
    String clientIp = getClientIp(request);
    return "rate_limit:ip:" + clientIp;
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // Extract first IP in case of multiple IPs via X-Forwarded-For
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip != null ? ip : "unknown";
  }
}
