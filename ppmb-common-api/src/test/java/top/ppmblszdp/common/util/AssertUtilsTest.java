package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("业务断言工具类测试")
class AssertUtilsTest {

  @Test
  @DisplayName("断言表达式为真")
  void testIsTrue() {
    assertDoesNotThrow(() -> AssertUtils.isTrue(true, CommonResultCode.SUCCESS), "表达式为真时不应抛出异常");
    IResultCode errorCode = CommonResultCode.PARAM_ERROR;
    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> AssertUtils.isTrue(false, errorCode),
            "表达式为假时应抛出 BusinessException");
    assertEquals(errorCode, ex.getResultCode(), "错误码应为 PARAM_ERROR");
  }

  @Test
  @DisplayName("断言对象不为空")
  void testNotNull() {
    Object obj = new Object();
    assertDoesNotThrow(() -> AssertUtils.notNull(obj, CommonResultCode.SUCCESS), "对象不为空时不应抛出异常");
    IResultCode errorCode = CommonResultCode.PARAM_ERROR;
    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> AssertUtils.notNull(null, errorCode),
            "对象为空时应抛出 BusinessException");
    assertEquals(errorCode, ex.getResultCode(), "错误码应为 PARAM_ERROR");
  }

  @Test
  @DisplayName("断言字符串不为空")
  void testNotEmptyString() {
    assertDoesNotThrow(
        () -> AssertUtils.notEmpty("not empty", CommonResultCode.SUCCESS), "字符串不为空时不应抛出异常");

    IResultCode errorCode = CommonResultCode.PARAM_ERROR;
    assertThrows(BusinessException.class, () -> AssertUtils.notEmpty("", errorCode), "空字符串应抛出异常");

    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty("  ", errorCode), "空白字符串应抛出异常");

    String nullStr = null;
    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty(nullStr, errorCode), "null 字符串应抛出异常");
  }

  @Test
  @DisplayName("断言集合不为空")
  void testNotEmptyCollection() {
    List<Integer> list = List.of(1);
    assertDoesNotThrow(() -> AssertUtils.notEmpty(list, CommonResultCode.SUCCESS), "集合不为空时不应抛出异常");

    List<Object> emptyList = Collections.emptyList();
    IResultCode errorCode = CommonResultCode.PARAM_ERROR;
    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty(emptyList, errorCode), "空集合应抛出异常");

    List<?> nullList = null;
    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty(nullList, errorCode), "null 集合应抛出异常");
  }

  @Test
  @DisplayName("断言 Map 不为空")
  void testNotEmptyMap() {
    Map<String, String> map = Map.of("k", "v");
    assertDoesNotThrow(() -> AssertUtils.notEmpty(map, CommonResultCode.SUCCESS), "Map 不为空时不应抛出异常");

    Map<Object, Object> emptyMap = Collections.emptyMap();
    IResultCode errorCode = CommonResultCode.PARAM_ERROR;
    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty(emptyMap, errorCode), "空 Map 应抛出异常");

    Map<?, ?> nullMap = null;
    assertThrows(
        BusinessException.class, () -> AssertUtils.notEmpty(nullMap, errorCode), "null Map 应抛出异常");
  }
}
