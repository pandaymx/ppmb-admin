package top.ppmblszdp.common.mq.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.mq.service.AuditLogService;

/** 审计应用事件监听器，负责将审计日志发送到 MQ。 */
@Component
@RequiredArgsConstructor
public class AuditApplicationEventListener {

  private final AuditLogService auditLogService;

  /**
   * 处理审计日志事件并分发到 MQ。
   *
   * @param event 审计日志事件
   */
  @Async("auditAsyncExecutor")
  @EventListener
  public void handleAuditLogEvent(AuditLogEvent event) {
    auditLogService.send(event.getAuditLogMessage());
  }
}
