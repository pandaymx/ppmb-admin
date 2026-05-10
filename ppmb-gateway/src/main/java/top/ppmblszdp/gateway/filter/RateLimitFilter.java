package top.ppmblszdp.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.gateway.config.GatewayRateLimitProperties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "ppmb.gateway.rate-limit",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RateLimitFilter extends OncePerRequestFilter implements Ordered {

  private final RedisRateLimiter redisRateLimiter;
  private final GatewayRateLimitProperties properties;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!properties.isEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = buildKey(request);

    boolean allowed =
        redisRateLimiter.isAllowed(
            "rate_limit:" + key, properties.getCount(), properties.getPeriod());

    if (!allowed) {
      log.warn("Rate limit exceeded for key: {}", key);
      handleRateLimitExceeded(response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String buildKey(HttpServletRequest request) {
    // 优先使用 Header 中的用户 ID
    String userId = request.getHeader("X-User-Id");
    if (userId != null && !userId.isEmpty()) {
      return "user:" + userId;
    }

    // fallback to IP address
    String ip = getIpAddress(request);
    return "ip:" + ip;
  }

  private String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip != null ? ip.split(",")[0] : "unknown";
  }

  private void handleRateLimitExceeded(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
    problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
    problemDetail.setTitle("Too Many Requests");

    // 遵循阿里业务码规范，放入自定义属性
    problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
    problemDetail.setProperty("timestamp", Instant.now());

    response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 100;
  }
}
