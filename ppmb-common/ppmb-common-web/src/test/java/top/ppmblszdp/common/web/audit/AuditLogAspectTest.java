package top.ppmblszdp.common.web.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ppmblszdp.common.api.annotation.AuditLog;
import top.ppmblszdp.common.api.annotation.Sensitive;
import top.ppmblszdp.common.api.event.AuditLogEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("审计日志切面测试")
class AuditLogAspectTest {

  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private ProceedingJoinPoint joinPoint;
  @Mock private MethodSignature methodSignature;

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("应记录请求信息并脱敏敏感参数")
  void shouldRecordAuditLogWithMaskedSensitiveParams() throws Throwable {
    AuditLogAspect aspect = new AuditLogAspect(eventPublisher);
    Method method = TestController.class.getDeclaredMethod("create", String.class, String.class);

    when(joinPoint.proceed()).thenReturn("ok");
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(method);
    when(methodSignature.getParameterNames()).thenReturn(new String[] {"username", "password"});
    when(joinPoint.getArgs()).thenReturn(new Object[] {"alice", "secret-123"});

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/users");
    request.setMethod("POST");
    request.setRemoteAddr("127.0.0.1");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    Object result = aspect.around(joinPoint);

    assertEquals("ok", result);
    ArgumentCaptor<AuditLogEvent> eventCaptor = ArgumentCaptor.forClass(AuditLogEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    AuditLogEvent event = eventCaptor.getValue();
    assertNotNull(event);
    assertEquals("创建用户", event.getAuditLogMessage().operationName());
    assertEquals("/users", event.getAuditLogMessage().requestUri());
    assertEquals("POST", event.getAuditLogMessage().requestMethod());
    assertEquals("127.0.0.1", event.getAuditLogMessage().ip());
    assertEquals(
        "{\"password\":\"***\",\"username\":\"alice\"}",
        event.getAuditLogMessage().requestParams());
  }

  static class TestController {
    @AuditLog("创建用户")
    public void create(String username, @Sensitive String password) {}
  }
}
