package top.ppmblszdp.gateway.filter.ratelimit;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.util.ReflectionUtils;

public class RateLimitFilterSupplier implements FilterSupplier {

  @Override
  public Collection<Method> get() {
    Method rateLimitMethod =
        ReflectionUtils.findMethod(
            RateLimitFilterFunctions.class, "rateLimit", String.class, String.class);
    if (rateLimitMethod != null) {
      return Collections.singletonList(rateLimitMethod);
    }
    return Collections.emptyList();
  }
}
