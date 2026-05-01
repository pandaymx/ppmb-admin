package top.ppmblszdp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;

/** 业务异常基类. */
@Getter
public class BusinessException extends RuntimeException {
  private final HttpStatus status;
  private final IResultCode resultCode;
  private final String detail;

  public BusinessException(String message) {
    this(HttpStatus.BAD_REQUEST, CommonResultCode.USER_ERROR, message, null);
  }

  public BusinessException(IResultCode resultCode) {
    this(HttpStatus.BAD_REQUEST, resultCode, resultCode.getMessage(), null);
  }

  public BusinessException(HttpStatus status, IResultCode resultCode) {
    this(status, resultCode, resultCode.getMessage(), null);
  }

  public BusinessException(
      HttpStatus status, IResultCode resultCode, String message, String detail) {
    super(message);
    this.status = status;
    this.resultCode = resultCode;
    this.detail = detail;
  }

  /**
   * 包含原因的构造函数，用于包装底层异常.
   *
   * @param resultCode 业务错误码
   * @param cause 原始异常
   */
  public BusinessException(IResultCode resultCode, Throwable cause) {
    super(resultCode.getMessage(), cause);
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    this.resultCode = resultCode;
    this.detail = cause.getMessage();
  }
}
