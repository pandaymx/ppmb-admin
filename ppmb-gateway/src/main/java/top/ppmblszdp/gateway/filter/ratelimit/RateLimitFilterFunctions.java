package top.ppmblszdp.gateway.filter.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.gateway.config.PrincipalNameKeyResolver;
import top.ppmblszdp.gateway.exception.RateLimitExceededException;

public interface RateLimitFilterFunctions {

  @Shortcut({"replenishRate", "burstCapacity"})
  static HandlerFilterFunction<ServerResponse, ServerResponse> rateLimit(
      String replenishRate, String burstCapacity) {
    return (request, next) -> {
      ApplicationContext context = MvcUtils.getApplicationContext(request);
      RedisRateLimiter redisRateLimiter = context.getBean(RedisRateLimiter.class);
      PrincipalNameKeyResolver keyResolver = context.getBean(PrincipalNameKeyResolver.class);

      HttpServletRequest servletRequest = request.servletRequest();
      String key = keyResolver.resolve(servletRequest);

      String routeId = "default";
      Object routeIdAttr = servletRequest.getAttribute(MvcUtils.GATEWAY_ROUTE_ID_ATTR);
      if (routeIdAttr != null) {
        routeId = routeIdAttr.toString();
      }

      String limitKey = "rate_limit:" + routeId + ":" + key;

      boolean allowed =
          redisRateLimiter.isAllowed(
              limitKey,
              Integer.parseInt(burstCapacity.trim()),
              Integer.parseInt(replenishRate.trim()));

      if (!allowed) {
        throw new RateLimitExceededException("您在短时间内发送了太多请求，请稍后再试。");
      }

      return next.handle(request);
    };
  }
}
