package top.ppmblszdp.common.security.data.aspect;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import top.ppmblszdp.common.security.data.DataPermissionContext;
import top.ppmblszdp.common.security.data.DataPermissionContextHolder;
import top.ppmblszdp.common.security.data.DataPermissionProvider;
import top.ppmblszdp.common.security.data.annotation.DataPermission;
import top.ppmblszdp.common.security.data.enums.DataScope;
import top.ppmblszdp.common.security.util.SecurityUtils;

@Aspect
@Order(100)
public class DataPermissionAspect {

  private static final Logger log = LoggerFactory.getLogger(DataPermissionAspect.class);

  @Autowired(required = false)
  private DataPermissionProvider dataPermissionProvider;

  @Around("@annotation(top.ppmblszdp.common.security.data.annotation.DataPermission)")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    DataPermission dataPermission = method.getAnnotation(DataPermission.class);

    Long userId = SecurityUtils.getUserId();

    if (userId == null || userId == 1L) {
      log.debug(
          "DataPermissionAspect: User ID not found or is default, proceeding without data permission filtering.");
      return point.proceed();
    }

    if (dataPermissionProvider == null) {
      log.warn(
          "DataPermissionProvider is not registered. DataPermission annotation will be ignored.");
      return point.proceed();
    }

    DataPermissionContext context =
        dataPermissionProvider.getPermissionContext(userId, dataPermission.permission());
    if (context == null) {
      context = DataPermissionContext.builder().dataScope(DataScope.SELF).userId(userId).build();
    }

    context.setDeptAlias(dataPermission.deptAlias());
    context.setUserAlias(dataPermission.userAlias());

    DataPermissionContextHolder.set(context);
    try {
      return point.proceed();
    } finally {
      DataPermissionContextHolder.clear();
    }
  }
}
