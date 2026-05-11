package top.ppmblszdp.gateway.filter.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.net.InetSocketAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.gateway.config.GatewayRateLimitProperties;
import org.springframework.web.servlet.DispatcherServlet;

public class RateLimitFilterFunctions {

    public static HandlerFilterFunction<ServerResponse, ServerResponse> myRateLimit() {
        return (request, next) -> {
            ApplicationContext context = (ApplicationContext) request.attributes()
                    .get(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            if (context == null) {
                return next.handle(request);
            }

            GatewayRateLimitProperties properties = context.getBean(GatewayRateLimitProperties.class);
            if (!properties.isEnabled()) {
                return next.handle(request);
            }

            RedisRateLimiter redisRateLimiter = context.getBean(RedisRateLimiter.class);

            String key = buildKey(request);
            boolean allowed = redisRateLimiter.isAllowed(
                    "rate_limit:" + key, properties.getCount(), properties.getPeriod());

            if (!allowed) {
                ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
                return handleRateLimitExceeded(objectMapper);
            }

            return next.handle(request);
        };
    }

    private static String buildKey(ServerRequest request) {
        String userId = request.headers().firstHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }

        String ip = getIpAddress(request);
        return "ip:" + ip;
    }

    private static String getIpAddress(ServerRequest request) {
        String ip = request.headers().firstHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().firstHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().firstHeader("WL-Proxy-Client-IP");
        }
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }

        Optional<InetSocketAddress> remoteAddress = request.remoteAddress();
        if (remoteAddress.isPresent()) {
            return remoteAddress.get().getAddress().getHostAddress();
        }

        return "unknown";
    }

    private static ServerResponse handleRateLimitExceeded(ObjectMapper objectMapper) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试。");
        problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setProperty("code", CommonResultCode.USER_ERROR.getCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problemDetail);
    }
}
