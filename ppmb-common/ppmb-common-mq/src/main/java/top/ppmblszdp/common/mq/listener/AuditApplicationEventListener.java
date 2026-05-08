package top.ppmblszdp.common.mq.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.mq.service.AuditLogService;

@Component
@RequiredArgsConstructor
public class AuditApplicationEventListener {

  private final AuditLogService auditLogService;

  @Async("auditAsyncExecutor")
  @EventListener
  public void handleAuditLogEvent(AuditLogEvent event) {
    auditLogService.send(event.getAuditLogMessage());
  }
}
