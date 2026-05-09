package top.ppmblszdp.gateway.exception;

import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

public class RateLimitExceededException extends BusinessException {

  public RateLimitExceededException() {
    super(
        HttpStatus.TOO_MANY_REQUESTS,
        CommonResultCode.USER_ERROR,
        "请求过于频繁",
        "您在短时间内发送了太多请求，请稍后再试。");
  }

  public RateLimitExceededException(String detail) {
    super(HttpStatus.TOO_MANY_REQUESTS, CommonResultCode.USER_ERROR, "请求过于频繁", detail);
  }
}
