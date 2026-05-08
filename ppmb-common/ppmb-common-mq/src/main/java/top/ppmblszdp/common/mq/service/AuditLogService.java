package top.ppmblszdp.common.mq.service;

import top.ppmblszdp.common.api.dto.AuditLogMessage;

/** 审计日志服务接口。 */
public interface AuditLogService {
  /**
   * 发送审计日志消息到 MQ。
   *
   * @param message 审计日志消息
   */
  void send(AuditLogMessage message);
}
