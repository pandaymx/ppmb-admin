package top.ppmblszdp.auth.domain.exception;

import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** 无效凭证异常（用户名或密码错误）. */
public class InvalidCredentialsException extends BusinessException {
  public InvalidCredentialsException() {
    super(HttpStatus.UNAUTHORIZED, CommonResultCode.UNAUTHORIZED, "用户名或密码错误", null);
  }
}
