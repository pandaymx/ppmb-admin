package top.ppmblszdp.gateway.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class GatewayExceptionHandlerTest {

  @Test
  public void testHandleRateLimitExceededException() {
    GatewayExceptionHandler handler = new GatewayExceptionHandler();
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/test");

    RateLimitExceededException ex = new RateLimitExceededException("Test detail");

    ProblemDetail problemDetail = handler.handleRateLimitExceededException(ex, request);

    assertNotNull(problemDetail);
    assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), problemDetail.getStatus());
    assertEquals("请求过于频繁", problemDetail.getTitle());
    assertEquals("Test detail", problemDetail.getDetail());
    assertEquals(
        URI.create("https://api.ppmb.com/errors/too-many-requests"), problemDetail.getType());
    assertEquals(URI.create("/api/test"), problemDetail.getInstance());
    assertNotNull(problemDetail.getProperties());
    assertEquals("A0001", problemDetail.getProperties().get("code"));
    assertNotNull(problemDetail.getProperties().get("timestamp"));
  }
}
