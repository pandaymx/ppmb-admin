package top.ppmblszdp.common.tenant;

import java.util.Optional;

/** Holder for the current tenant context. */
public class TenantContextHolder {

  private static final ThreadLocal<Long> TENANT_CONTEXT = new ThreadLocal<>();

  private TenantContextHolder() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Sets the tenant ID.
   *
   * @param tenantId the tenant ID
   */
  public static void set(Long tenantId) {
    TENANT_CONTEXT.set(tenantId);
  }

  /**
   * Gets the tenant ID.
   *
   * @return the tenant ID Optional
   */
  public static Optional<Long> get() {
    return Optional.ofNullable(TENANT_CONTEXT.get());
  }

  /** Clears the tenant context. */
  public static void clear() {
    TENANT_CONTEXT.remove();
  }
}
