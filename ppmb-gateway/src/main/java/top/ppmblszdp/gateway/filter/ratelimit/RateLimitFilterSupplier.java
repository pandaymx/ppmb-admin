package top.ppmblszdp.gateway.filter.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.lang.reflect.Method;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.RequestContextUtils;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;

@Component
public class RateLimitFilterSupplier implements FilterSupplier {

    private static volatile RedisRateLimiter cachedRedisRateLimiter;

    @Override
    public Collection<Method> get() {
        return Arrays.asList(RateLimitFilterSupplier.class.getMethods());
    }

    public static HandlerFilterFunction<ServerResponse, ServerResponse> dynamicRateLimit(int count, int period) {
        return (request, next) -> {
            HttpServletRequest servletRequest = request.servletRequest();
            RedisRateLimiter redisRateLimiter = getRedisRateLimiter(servletRequest);

            if (redisRateLimiter == null) {
                 return next.handle(request);
            }

            String key = buildKey(servletRequest);

            boolean allowed = redisRateLimiter.isAllowed(
                "rate_limit:" + key, count, period);

            if (!allowed) {
                return handleRateLimitExceeded();
            }

            return next.handle(request);
        };
    }

    private static RedisRateLimiter getRedisRateLimiter(HttpServletRequest request) {
        if (cachedRedisRateLimiter == null) {
            synchronized (RateLimitFilterSupplier.class) {
                if (cachedRedisRateLimiter == null) {
                    ApplicationContext context = RequestContextUtils.findWebApplicationContext(request);
                    if (context != null) {
                        cachedRedisRateLimiter = context.getBean(RedisRateLimiter.class);
                    }
                }
            }
        }
        return cachedRedisRateLimiter;
    }

    private static String buildKey(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }

        String ip = getIpAddress(request);
        return "ip:" + ip;
    }

    private static String getIpAddress(HttpServletRequest request) {
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

    private static ServerResponse handleRateLimitExceeded() {
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
        problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(problemDetail);
    }
}
