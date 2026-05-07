package top.ppmblszdp.common.api.service;

import top.ppmblszdp.common.api.dto.ExceptionLogMessage;

/** 异常日志记录服务接口. */
public interface ExceptionLogService {

  /**
   * 发送异常日志.
   *
   * @param message 异常日志消息
   */
  void send(ExceptionLogMessage message);
}
