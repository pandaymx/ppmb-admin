package top.ppmblszdp.system.interfaces.mq;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.log.SysErrorLogMessage;
import top.ppmblszdp.common.mq.CommonMessage;
import top.ppmblszdp.system.domain.model.log.entity.SysErrorLog;
import top.ppmblszdp.system.infrastructure.persistence.log.repository.SysErrorLogRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogConsumer {

  private final SysErrorLogRepository sysErrorLogRepository;

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "q.ppmb.system.error.log", durable = "true"),
              exchange = @Exchange(value = "ex.ppmb.sys.log", type = "topic"),
              key = "rk.ppmb.sys.log.error"))
  @Transactional
  public void consumeErrorLog(CommonMessage<SysErrorLogMessage> message) {
    try {
      SysErrorLogMessage payload = message.getPayload();
      if (payload == null) {
        log.warn("Received empty error log message: {}", message.getMessageId());
        return;
      }

      SysErrorLog errorLog = new SysErrorLog();
      errorLog.setTenantId(0L);
      errorLog.setDelFlag(0);
      errorLog.setVersion(0);
      errorLog.setCreateTime(LocalDateTime.now());
      errorLog.setCreateBy(payload.getOperatorId() != null ? payload.getOperatorId() : 0L);

      errorLog.setRequestUrl(payload.getRequestUrl());
      errorLog.setHttpMethod(payload.getHttpMethod());
      errorLog.setClientIp(payload.getClientIp());
      errorLog.setUserAgent(payload.getUserAgent());
      errorLog.setRequestParams(payload.getRequestParams());

      errorLog.setOperatorId(payload.getOperatorId());
      errorLog.setOperatorAccount(payload.getOperatorAccount());

      errorLog.setClassName(payload.getClassName());
      errorLog.setMethodName(payload.getMethodName());
      errorLog.setExceptionType(payload.getExceptionType());
      errorLog.setErrorMessage(payload.getErrorMessage());
      errorLog.setStackTrace(payload.getStackTrace());

      sysErrorLogRepository.save(errorLog);
      log.debug("Successfully saved error log, messageId: {}", message.getMessageId());
    } catch (Exception e) {
      log.error("Failed to process error log message: {}", message.getMessageId(), e);
    }
  }
}
