package top.ppmblszdp.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("Feign 异常解码器测试")
class FeignErrorDecoderTest {

  private FeignErrorDecoder feignErrorDecoder;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    feignErrorDecoder = new FeignErrorDecoder(objectMapper);
  }

  @Test
  @DisplayName("应成功解析远程服务的 ProblemDetail 并转换为 BusinessException")
  void shouldDecodeProblemDetailCorrectly() {
    // Arrange
    String problemDetailJson =
        """
        {
          "type": "about:blank",
          "title": "Bad Request",
          "status": 400,
          "detail": "Invalid parameter",
          "instance": "/api/users",
          "code": "A0400"
        }
        """;

    Response response =
        Response.builder()
            .status(400)
            .reason("Bad Request")
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "/api/users",
                    Collections.emptyMap(),
                    null,
                    StandardCharsets.UTF_8,
                    null))
            .body(problemDetailJson, StandardCharsets.UTF_8)
            .headers(Collections.emptyMap())
            .build();

    // Act
    Exception result = feignErrorDecoder.decode("userService#getUser", response);

    // Assert
    assertTrue(result instanceof BusinessException);
    BusinessException ex = (BusinessException) result;
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    assertEquals("A0400", ex.getResultCode().getCode());
    assertEquals("Bad Request", ex.getResultCode().getMessage());
    assertEquals("Invalid parameter", ex.getDetail());
  }

  @Test
  @DisplayName("当响应体为空时应返回默认的远程服务异常")
  void shouldReturnDefaultExceptionWhenBodyIsEmpty() {
    // Arrange
    Response response =
        Response.builder()
            .status(500)
            .reason("Internal Server Error")
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "/api/users",
                    Collections.emptyMap(),
                    null,
                    StandardCharsets.UTF_8,
                    null))
            .body((byte[]) null)
            .headers(Collections.emptyMap())
            .build();

    // Act
    Exception result = feignErrorDecoder.decode("userService#getUser", response);

    // Assert
    assertTrue(result instanceof BusinessException);
    BusinessException ex = (BusinessException) result;
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    assertEquals("C0001", ex.getResultCode().getCode());
  }
}
