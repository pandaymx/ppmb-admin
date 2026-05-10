package top.ppmblszdp.common.security.data;

import java.util.Optional;

/** 数据权限上下文. */
public class DataPermissionContextHolder {

  private static final ThreadLocal<DataPermissionContext> CONTEXT = new ThreadLocal<>();

  private DataPermissionContextHolder() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void set(DataPermissionContext context) {
    CONTEXT.set(context);
  }

  public static Optional<DataPermissionContext> get() {
    return Optional.ofNullable(CONTEXT.get());
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
