package top.ppmblszdp.system.infrastructure.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.system.domain.model.log.entity.SysAuditLog;
import top.ppmblszdp.system.domain.model.log.repository.AuditLogRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogListener {

  private final AuditLogRepository auditLogRepository;

  @RabbitListener(queues = MqConstants.AUDIT_LOG_QUEUE)
  @Transactional(rollbackFor = Exception.class)
  public void onAuditLogMessage(AuditLogMessage message) {
    try {
      if (auditLogRepository.existsByTraceId(message.traceId())) {
        log.warn("收到重复的审计日志消息, traceId: {}", message.traceId());
        return;
      }
      SysAuditLog logEntity = new SysAuditLog();
      logEntity.setTraceId(message.traceId());
      logEntity.setOperationName(message.operationName());
      logEntity.setEntityName(message.entityName());
      logEntity.setEntityId(message.entityId());
      logEntity.setOldValue(message.oldValue());
      logEntity.setNewValue(message.newValue());
      logEntity.setRequestUri(message.requestUri());
      logEntity.setRequestMethod(message.requestMethod());
      logEntity.setRequestParams(message.requestParams());
      logEntity.setIp(message.ip());
      logEntity.setUserId(message.userId());
      logEntity.setCreateTime(message.createTime());

      auditLogRepository.save(logEntity);
    } catch (Exception e) {
      log.error("保存审计日志失败: {}", message, e);
      // Let it throw so MQ can retry based on configured policy
      throw e;
    }
  }
}
