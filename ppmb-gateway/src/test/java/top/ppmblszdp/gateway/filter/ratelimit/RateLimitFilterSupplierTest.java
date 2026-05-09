package top.ppmblszdp.gateway.filter.ratelimit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import org.junit.jupiter.api.Test;

public class RateLimitFilterSupplierTest {

  @Test
  public void testGet() {
    RateLimitFilterSupplier supplier = new RateLimitFilterSupplier();
    Collection<Method> methods = supplier.get();
    assertNotNull(methods);
    assertFalse(methods.isEmpty());
  }
}
