package top.ppmblszdp.common.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("Feign 异常解码器测试")
class FeignErrorDecoderTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FeignErrorDecoder decoder = new FeignErrorDecoder(objectMapper);

  @Test
  @DisplayName("测试解析正确的 ProblemDetail 响应")
  void testDecodeProblemDetail() {
    String body = "{\"title\":\"Business Error\", \"detail\":\"Some detail\", \"code\":\"40001\"}";
    Response response =
        Response.builder()
            .status(400)
            .reason("Bad Request")
            .request(mock(feign.Request.class))
            .body(body, StandardCharsets.UTF_8)
            .build();

    Exception exception = decoder.decode("testMethod", response);

    assertTrue(exception instanceof BusinessException);
    BusinessException bizEx = (BusinessException) exception;
    assertEquals(HttpStatus.BAD_REQUEST, bizEx.getStatus());
    assertEquals("40001", bizEx.getResultCode().getCode());
    assertEquals("Business Error", bizEx.getResultCode().getMessage());
    assertEquals("Some detail", bizEx.getDetail());
  }

  @Test
  @DisplayName("测试当响应体为空时")
  void testDecodeEmptyBody() {
    Response response =
        Response.builder()
            .status(500)
            .reason("Internal Server Error")
            .request(mock(feign.Request.class))
            .build();

    Exception exception = decoder.decode("testMethod", response);

    assertTrue(exception instanceof BusinessException);
    BusinessException bizEx = (BusinessException) exception;
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bizEx.getStatus());
    assertEquals("C0001", bizEx.getResultCode().getCode());
  }

  @Test
  @DisplayName("测试当解析响应体失败时")
  void testDecodeInvalidBody() {
    Response response =
        Response.builder()
            .status(400)
            .reason("Bad Request")
            .request(mock(feign.Request.class))
            .body("invalid json", StandardCharsets.UTF_8)
            .build();

    Exception exception = decoder.decode("testMethod", response);

    assertTrue(exception instanceof BusinessException);
    BusinessException bizEx = (BusinessException) exception;
    assertEquals(HttpStatus.BAD_REQUEST, bizEx.getStatus());
    assertEquals("C0001", bizEx.getResultCode().getCode());
  }

  @Test
  @DisplayName("测试当响应为非 JSON 格式（如负载均衡错误）时")
  void testDecodeLoadBalancerError() {
    String body = "Load balancer does not contain an instance for the service ppmb-system";
    Response response =
        Response.builder()
            .status(503)
            .reason("Service Unavailable")
            .request(mock(feign.Request.class))
            .body(body, StandardCharsets.UTF_8)
            .build();

    Exception exception = decoder.decode("testMethod", response);

    assertTrue(exception instanceof BusinessException);
    BusinessException bizEx = (BusinessException) exception;
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, bizEx.getStatus());
    assertEquals("C0001", bizEx.getResultCode().getCode());
    assertTrue(bizEx.getDetail().contains(body));
  }
}
