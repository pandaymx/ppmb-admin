package top.ppmblszdp.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;

@DisplayName("业务异常类测试")
class BusinessExceptionTest {

  @Test
  @DisplayName("应该支持单一消息构造")
  void testMessageConstructor() {
    String message = "test error";
    BusinessException ex = new BusinessException(message);
    assertEquals(message, ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    assertEquals(CommonResultCode.USER_ERROR, ex.getResultCode());
    assertNull(ex.getDetail());
  }

  @Test
  @DisplayName("应该支持错误码构造")
  void testResultCodeConstructor() {
    BusinessException ex = new BusinessException(CommonResultCode.PARAM_ERROR);
    assertEquals(CommonResultCode.PARAM_ERROR.getMessage(), ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    assertEquals(CommonResultCode.PARAM_ERROR, ex.getResultCode());
  }

  @Test
  @DisplayName("应该支持状态码和错误码构造")
  void testStatusAndResultCodeConstructor() {
    BusinessException ex =
        new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, CommonResultCode.SYSTEM_ERROR);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    assertEquals(CommonResultCode.SYSTEM_ERROR, ex.getResultCode());
  }

  @Test
  @DisplayName("应该支持完整参数构造")
  void testFullConstructor() {
    String detail = "more info";
    BusinessException ex =
        new BusinessException(
            HttpStatus.FORBIDDEN, CommonResultCode.USER_ERROR, "Forbidden access", detail);
    assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    assertEquals(CommonResultCode.USER_ERROR, ex.getResultCode());
    assertEquals("Forbidden access", ex.getMessage());
    assertEquals(detail, ex.getDetail());
  }

  @Test
  @DisplayName("应该支持异常包装构造")
  void testCauseConstructor() {
    RuntimeException cause = new RuntimeException("root cause");
    BusinessException ex = new BusinessException(CommonResultCode.SYSTEM_ERROR, cause);
    assertEquals(CommonResultCode.SYSTEM_ERROR, ex.getResultCode());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    assertEquals(cause, ex.getCause());
    assertEquals("root cause", ex.getDetail());
  }
}
