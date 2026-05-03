package top.ppmblszdp.common.api;

import java.io.Serializable;
import java.time.Instant;

/**
 * 统一响应结构.
 *
 * @param <T> 数据类型
 */
public record Result<T>(String code, String message, T data, Instant timestamp)
    implements Serializable {

  public static <T> Result<T> success(T data) {
    return new Result<>(
        CommonResultCode.SUCCESS.getCode(),
        CommonResultCode.SUCCESS.getMessage(),
        data,
        Instant.now());
  }

  public static <T> Result<T> success() {
    return success(null);
  }

  public static <T> Result<T> failure(IResultCode resultCode) {
    return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, Instant.now());
  }

  public static <T> Result<T> failure(String code, String message) {
    return new Result<>(code, message, null, Instant.now());
  }
}
