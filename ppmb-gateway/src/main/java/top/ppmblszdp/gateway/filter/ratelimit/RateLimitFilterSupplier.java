package top.ppmblszdp.gateway.filter.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.gateway.config.GatewayRateLimitProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilterSupplier implements FilterSupplier {

  private final RedisRateLimiter redisRateLimiter;
  private final GatewayRateLimitProperties properties;
  private final ObjectMapper objectMapper;

  @Override
  public Collection<Method> get() {
    return Arrays.asList(RateLimitFilterSupplier.class.getMethods());
  }

  public HandlerFilterFunction<ServerResponse, ServerResponse> rateLimit() {
    return (request, next) -> {
      if (!properties.isEnabled()) {
        return next.handle(request);
      }

      String key = buildKey(request);

      boolean allowed =
          redisRateLimiter.isAllowed(
              "rate_limit:" + key, properties.getCount(), properties.getPeriod());

      if (!allowed) {
        log.warn("Rate limit exceeded for key: {}", key);
        return handleRateLimitExceeded();
      }

      return next.handle(request);
    };
  }

  private String buildKey(ServerRequest request) {
    // 优先使用 Header 中的用户 ID
    String userId = request.headers().firstHeader("X-User-Id");
    if (userId != null && !userId.isEmpty()) {
      return "user:" + userId;
    }

    // fallback to IP address
    String ip = getIpAddress(request);
    return "ip:" + ip;
  }

  private String getIpAddress(ServerRequest request) {
    String ip = request.headers().firstHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.headers().firstHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.headers().firstHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      if (request.servletRequest() != null) {
          ip = request.servletRequest().getRemoteAddr();
      }
    }
    return ip != null ? ip.split(",")[0] : "unknown";
  }

  private ServerResponse handleRateLimitExceeded() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
    problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
    problemDetail.setTitle("Too Many Requests");

    // 遵循阿里业务码规范，放入自定义属性
    problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
    problemDetail.setProperty("timestamp", Instant.now());

    try {
        String body = objectMapper.writeValueAsString(problemDetail);
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
    } catch (Exception e) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
  }
}
