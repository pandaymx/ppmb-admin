package top.ppmblszdp.common.mq.listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.mq.service.AuditLogService;

@Component
@RequiredArgsConstructor
public class AuditApplicationEventListener {

  private final AuditLogService auditLogService;
  // Use Virtual Threads for async processing
  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  @EventListener
  public void handleAuditLogEvent(AuditLogEvent event) {
    executorService.submit(() -> auditLogService.send(event.getAuditLogMessage()));
  }
}
