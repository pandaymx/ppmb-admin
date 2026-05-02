package top.ppmblszdp.common.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  void testSuccess() {
    Result<String> result = Result.success("test data");
    assertEquals(CommonResultCode.SUCCESS.getCode(), result.code());
    assertEquals("test data", result.data());
    assertNotNull(result.timestamp());
  }

  @Test
  void testSuccessEmpty() {
    Result<Void> result = Result.success();
    assertEquals(CommonResultCode.SUCCESS.getCode(), result.code());
    assertNull(result.data());
  }

  @Test
  void testFailure() {
    Result<Void> result = Result.failure(CommonResultCode.USER_ERROR);
    assertEquals(CommonResultCode.USER_ERROR.getCode(), result.code());
    assertNull(result.data());
    assertNotNull(result.timestamp());
  }

  @Test
  void testFailureCustom() {
    Result<Void> result = Result.failure("500", "Custom Error");
    assertEquals("500", result.code());
    assertEquals("Custom Error", result.message());
  }
}
