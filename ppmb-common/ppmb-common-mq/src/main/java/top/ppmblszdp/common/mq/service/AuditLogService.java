package top.ppmblszdp.common.mq.service;

import top.ppmblszdp.common.api.dto.AuditLogMessage;

public interface AuditLogService {
  void send(AuditLogMessage message);
}
