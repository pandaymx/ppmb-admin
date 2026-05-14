package top.ppmblszdp.gateway.filter.ratelimit;

import java.lang.reflect.Method;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.gateway.config.GatewayRateLimitProperties;

@Component
public class RateLimitFilterSupplier implements FilterSupplier {

  @Override
  public Collection<Method> get() {
    try {
      return Collections.singletonList(RateLimitFilterSupplier.class.getMethod("rateLimit"));
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static HandlerFilterFunction<ServerResponse, ServerResponse> rateLimit() {
    return (request, next) -> {
      ApplicationContext context = MvcUtils.getApplicationContext(request);
      if (context == null) {
        return next.handle(request);
      }
      RedisRateLimiter redisRateLimiter = context.getBean(RedisRateLimiter.class);
      GatewayRateLimitProperties properties = context.getBean(GatewayRateLimitProperties.class);

      if (!properties.isEnabled()) {
        return next.handle(request);
      }

      String userId = request.headers().firstHeader("X-User-Id");
      String key;
      if (userId != null && !userId.isEmpty()) {
        key = "user:" + userId;
      } else {
        String ip = getIpAddress(request);
        key = "ip:" + ip;
      }

      boolean allowed =
          redisRateLimiter.isAllowed(
              "rate_limit:" + key, properties.getCount(), properties.getPeriod());

      if (!allowed) {
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
        problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).body(problemDetail);
      }

      return next.handle(request);
    };
  }

  private static String getIpAddress(
      org.springframework.web.servlet.function.ServerRequest request) {
    String ip = request.headers().firstHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.headers().firstHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.headers().firstHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.remoteAddress().map(addr -> addr.getHostString()).orElse("unknown");
    }
    return ip != null ? ip.split(",")[0].trim() : "unknown";
  }
}
