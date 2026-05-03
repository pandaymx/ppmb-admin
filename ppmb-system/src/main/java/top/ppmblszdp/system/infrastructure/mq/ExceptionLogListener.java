package top.ppmblszdp.system.infrastructure.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.system.domain.model.log.entity.SysExceptionLog;
import top.ppmblszdp.system.domain.model.log.repository.ExceptionLogRepository;

/** 异常日志 MQ 监听器. */
@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionLogListener {

  private final ExceptionLogRepository exceptionLogRepository;

  @RabbitListener(queues = MqConstants.EXCEPTION_QUEUE)
  public void handleExceptionLog(ExceptionLogMessage message) {
    log.info("接收到异常日志消息: {} - {}", message.serviceName(), message.exceptionName());
    try {
      SysExceptionLog logEntity = new SysExceptionLog();
      logEntity.setServiceName(message.serviceName());
      logEntity.setExceptionName(message.exceptionName());
      logEntity.setMessage(message.message());
      logEntity.setStackTrace(message.stackTrace());
      logEntity.setRequestUri(message.requestUri());
      logEntity.setRequestMethod(message.requestMethod());
      logEntity.setRequestParams(message.requestParams());
      logEntity.setIp(message.ip());
      logEntity.setUserId(message.userId());
      logEntity.setCreateTime(message.createTime());

      exceptionLogRepository.save(logEntity);
    } catch (Exception e) {
      log.error("保存异常日志到数据库失败", e);
    }
  }
}
