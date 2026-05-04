package top.ppmblszdp.auth.domain.exception;

import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** 账号已禁用异常. */
public class AccountDisabledException extends BusinessException {
  public AccountDisabledException() {
    super(HttpStatus.FORBIDDEN, CommonResultCode.USER_ERROR, "账号已被禁用", null);
  }
}
