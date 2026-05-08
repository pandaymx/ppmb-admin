package top.ppmblszdp.common.mq.exception;

import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** MQ 模块异常. */
public class MqException extends BusinessException {
  private static final long serialVersionUID = 1L;

  public MqException(String message) {
    super(message);
  }

  public MqException(String message, Throwable cause) {
    super(CommonResultCode.REMOTE_ERROR, cause);
  }
}
