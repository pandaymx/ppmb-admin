package top.ppmblszdp.common.util;

import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** 业务断言工具类，减少 if-throw 代码. */
public final class AssertUtils {

  private AssertUtils() {}

  /**
   * 断言表达式为真，否则抛出业务异常.
   *
   * @param expression 表达式
   * @param code 错误码
   */
  public static void isTrue(boolean expression, IResultCode code) {
    if (!expression) {
      throw new BusinessException(code);
    }
  }

  /**
   * 断言对象不为空，否则抛出业务异常.
   *
   * @param object 对象
   * @param code 错误码
   */
  public static void notNull(Object object, IResultCode code) {
    if (object == null) {
      throw new BusinessException(code);
    }
  }

  /**
   * 断言字符串不为空，否则抛出业务异常.
   *
   * @param text 字符串
   * @param code 错误码
   */
  public static void notEmpty(String text, IResultCode code) {
    if (text == null || text.isBlank()) {
      throw new BusinessException(code);
    }
  }

  /**
   * 断言集合不为空，否则抛出业务异常.
   *
   * @param collection 集合
   * @param code 错误码
   */
  public static void notEmpty(java.util.Collection<?> collection, IResultCode code) {
    if (collection == null || collection.isEmpty()) {
      throw new BusinessException(code);
    }
  }

  /**
   * 断言 Map 不为空，否则抛出业务异常.
   *
   * @param map Map
   * @param code 错误码
   */
  public static void notEmpty(java.util.Map<?, ?> map, IResultCode code) {
    if (map == null || map.isEmpty()) {
      throw new BusinessException(code);
    }
  }
}
