package top.ppmblszdp.common.api.event;

import org.springframework.context.ApplicationEvent;
import top.ppmblszdp.common.api.dto.AuditLogMessage;

/** 审计日志事件. */
public class AuditLogEvent extends ApplicationEvent {

  private final AuditLogMessage auditLogMessage;

  /**
   * 构造审计日志事件.
   *
   * @param source 事件源.
   * @param auditLogMessage 审计日志消息.
   */
  public AuditLogEvent(Object source, AuditLogMessage auditLogMessage) {
    super(source);
    this.auditLogMessage = auditLogMessage;
  }

  /**
   * 获取审计日志消息.
   *
   * @return 审计日志消息.
   */
  public AuditLogMessage getAuditLogMessage() {
    return auditLogMessage;
  }
}
