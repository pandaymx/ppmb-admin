package top.ppmblszdp.common.mq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.api.service.ExceptionLogService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqAuditLogServiceImpl implements AuditLogService {

  private final RabbitTemplate rabbitTemplate;
  private final ExceptionLogService exceptionLogService;

  @Override
  public void send(AuditLogMessage message) {
    try {
      rabbitTemplate.convertAndSend(
          MqConstants.AUDIT_LOG_EXCHANGE, MqConstants.AUDIT_LOG_ROUTING_KEY, message);
    } catch (Exception e) {
      log.error("发送审计日志到 MQ 失败", e);
      // Fallback to exception log
      ExceptionLogMessage exceptionMessage = new ExceptionLogMessage(
        "ppmb-common-mq",
        e.getClass().getName(),
        "Failed to send audit log: " + e.getMessage(),
        e.toString(),
        message.requestUri(),
        message.requestMethod(),
        message.requestParams(),
        message.ip(),
        message.userId(),
        LocalDateTime.now()
      );
      exceptionLogService.send(exceptionMessage);
    }
  }
}
