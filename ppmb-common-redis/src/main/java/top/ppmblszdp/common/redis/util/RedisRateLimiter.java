package top.ppmblszdp.common.redis.util;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "ppmb.redis.rate-limiter",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RedisRateLimiter {

  private final StringRedisTemplate stringRedisTemplate;
  private DefaultRedisScript<Long> limitScript;

  /**
   * Checks if the request is allowed based on limit and time window.
   *
   * @param key the rate limit key
   * @param count the max allowed requests
   * @param period the time window in seconds
   * @return true if allowed, false if limit exceeded
   */
  public boolean isAllowed(String key, int count, int period) {
    if (limitScript == null) {
      limitScript = new DefaultRedisScript<>();
      limitScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/limit.lua")));
      limitScript.setResultType(Long.class);
    }

    Long result =
        stringRedisTemplate.execute(
            limitScript,
            Collections.singletonList(key),
            String.valueOf(count),
            String.valueOf(period));

    return result != null && result == 1L;
  }
}
