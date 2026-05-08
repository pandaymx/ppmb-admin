package top.ppmblszdp.common.web.audit;

import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ppmblszdp.common.api.annotation.AuditLog;
import top.ppmblszdp.common.api.annotation.Sensitive;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.security.util.SecurityUtils;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

  private final ApplicationEventPublisher eventPublisher;
  private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

  @Around("@annotation(top.ppmblszdp.common.api.annotation.AuditLog)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = null;
    try {
      result = joinPoint.proceed();
      return result;
    } finally {
      try {
        recordAuditLog(joinPoint);
      } catch (Exception e) {
        log.error("Failed to record audit log", e);
      }
    }
  }

  private void recordAuditLog(ProceedingJoinPoint joinPoint) throws Exception {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    AuditLog auditLogAnnotation = method.getAnnotation(AuditLog.class);
    String operationName =
        auditLogAnnotation != null ? auditLogAnnotation.value() : method.getName();

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String requestUri = null;
    String requestMethod = null;
    String ip = null;
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      requestUri = request.getRequestURI();
      requestMethod = request.getMethod();
      ip = request.getRemoteAddr();
    }

    String[] parameterNames = signature.getParameterNames();
    Parameter[] parameters = method.getParameters();
    Object[] args = joinPoint.getArgs();
    Map<String, Object> paramMap = new HashMap<>();

    if (parameterNames != null && args != null) {
      for (int i = 0; i < parameterNames.length; i++) {
        String paramName = parameterNames[i];
        Object arg = args[i];

        boolean isSensitive = parameters[i].isAnnotationPresent(Sensitive.class);

        if (isSensitive
            || paramName.toLowerCase().contains("password")
            || paramName.toLowerCase().contains("secret")
            || paramName.toLowerCase().contains("token")) {
          paramMap.put(paramName, "***");
        } else {
          paramMap.put(paramName, arg);
        }
      }
    }
    String requestParams = jsonMapper.writeValueAsString(paramMap);

    AuditLogMessage message =
        new AuditLogMessage(
            UUID.randomUUID().toString(),
            operationName,
            null,
            null,
            null,
            null,
            requestUri,
            requestMethod,
            requestParams,
            ip,
            SecurityUtils.getUserId(),
            LocalDateTime.now());

    eventPublisher.publishEvent(new AuditLogEvent(this, message));
  }
}
