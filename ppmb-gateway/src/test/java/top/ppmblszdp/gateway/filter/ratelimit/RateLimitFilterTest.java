package top.ppmblszdp.gateway.filter.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.common.redis.util.RedisUtil;
import top.ppmblszdp.gateway.GatewayApplication;
import top.ppmblszdp.gateway.exception.RateLimitExceededException;

@SpringBootTest(classes = GatewayApplication.class)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "spring.cloud.consul.discovery.enabled=false",
      "spring.cloud.consul.config.enabled=false",
      "spring.cloud.consul.enabled=false",
      "spring.cloud.consul.discovery.register=false",
      "spring.cloud.gateway.server.webmvc.routes=",
      "spring.autoconfigure.exclude=top.ppmblszdp.common.redis.config.PpmbRedisAutoConfiguration"
    })
public class RateLimitFilterTest {

  @MockitoBean private RedisRateLimiter redisRateLimiter;
  @MockitoBean private RedisUtil redisUtil;

  @Autowired private ApplicationContext context;

  @Test
  public void testRateLimitAllowed() throws Exception {
    when(redisRateLimiter.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(true);

    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.addHeader("X-User-Id", "test-user-1");
    servletRequest.setAttribute(
        org.springframework.web.servlet.DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        context);

    ServerRequest request = mock(ServerRequest.class);
    when(request.servletRequest()).thenReturn(servletRequest);

    HandlerFunction<ServerResponse> next = req -> mock(ServerResponse.class);

    final HandlerFilterFunction<ServerResponse, ServerResponse> filter =
        RateLimitFilterFunctions.rateLimit("10", "20");

    ServerResponse response = filter.filter(request, next);
  }

  @Test
  public void testRateLimitExceeded() throws Exception {
    when(redisRateLimiter.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(false);

    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.addHeader("X-User-Id", "test-user-2");
    servletRequest.setAttribute(
        org.springframework.web.servlet.DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        context);

    ServerRequest request = mock(ServerRequest.class);
    when(request.servletRequest()).thenReturn(servletRequest);

    HandlerFunction<ServerResponse> next = req -> mock(ServerResponse.class);

    final HandlerFilterFunction<ServerResponse, ServerResponse> filter =
        RateLimitFilterFunctions.rateLimit("10", "20");

    RateLimitExceededException ex =
        assertThrows(
            RateLimitExceededException.class,
            () -> {
              filter.filter(request, next);
            });
    assertEquals(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, ex.getStatus());
  }

  @Test
  public void testRateLimitExceptionConstructors() {
    RateLimitExceededException ex1 = new RateLimitExceededException();
    assertEquals("您在短时间内发送了太多请求，请稍后再试。", ex1.getDetail());
  }
}
