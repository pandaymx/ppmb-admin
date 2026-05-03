package top.ppmblszdp.common.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@DisplayName("Feign 配置类测试")
class FeignConfigTest {

  @InjectMocks private FeignConfig feignConfig;

  @Test
  @DisplayName("测试 ErrorDecoder Bean 注册")
  void testErrorDecoder() {
    ObjectMapper mapper = new ObjectMapper();
    ErrorDecoder decoder = feignConfig.errorDecoder(mapper);
    assertNotNull(decoder);
    assertTrue(decoder instanceof FeignErrorDecoder);
  }

  @Test
  @DisplayName("测试 Feign 日志级别")
  void testFeignLoggerLevel() {
    assertEquals(Logger.Level.FULL, feignConfig.feignLoggerLevel());
  }

  @Test
  @DisplayName("测试 RequestInterceptor 透传请求头")
  void testRequestInterceptor() {
    // Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    Vector<String> headers = new Vector<>();
    headers.add("Authorization");
    headers.add("X-User-Id");
    headers.add("Content-Type");

    when(request.getHeaderNames()).thenReturn(headers.elements());
    when(request.getHeader("Authorization")).thenReturn("Bearer token");
    when(request.getHeader("X-User-Id")).thenReturn("1");

    RequestInterceptor interceptor = feignConfig.requestInterceptor();
    RequestTemplate template = new RequestTemplate();

    // Act
    interceptor.apply(template);

    // Assert
    assertTrue(template.headers().containsKey("Authorization"));
    assertTrue(template.headers().containsKey("X-User-Id"));
    assertFalse(template.headers().containsKey("Content-Type"));
    assertEquals("Bearer token", template.headers().get("Authorization").iterator().next());
    assertEquals("1", template.headers().get("X-User-Id").iterator().next());

    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("测试 RequestInterceptor 当 headerNames 为空时")
  void testRequestInterceptorWithNullHeaderNames() {
    // Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    when(request.getHeaderNames()).thenReturn(null);

    RequestInterceptor interceptor = feignConfig.requestInterceptor();
    RequestTemplate template = new RequestTemplate();

    // Act
    interceptor.apply(template);

    // Assert
    assertTrue(template.headers().isEmpty());

    RequestContextHolder.resetRequestAttributes();
  }
}
