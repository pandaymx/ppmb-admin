package top.ppmblszdp.gateway.filter.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class RateLimitFilterFunctions {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilterFunctions.class);
    private static RedisRateLimiter cachedLimiter;

    public static HandlerFilterFunction<ServerResponse, ServerResponse> rateLimit(int count, int period) {
        return (request, next) -> {
            HttpServletRequest servletRequest = request.servletRequest();

            if (cachedLimiter == null) {
                ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletRequest.getServletContext());
                cachedLimiter = context.getBean(RedisRateLimiter.class);
            }

            String key = buildKey(servletRequest);

            boolean allowed = cachedLimiter.isAllowed(
                    "rate_limit:" + key, count, period);

            if (!allowed) {
                log.warn("Rate limit exceeded for key: {}", key);
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
                problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
                problemDetail.setTitle("Too Many Requests");
                problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
                problemDetail.setProperty("timestamp", Instant.now());

                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(problemDetail);
            }

            return next.handle(request);
        };
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
}
