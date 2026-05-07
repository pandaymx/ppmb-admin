package top.ppmblszdp.common.mq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.api.service.ExceptionLogService;

/** 基于 MQ 的异常日志记录实现. */
@Service
@RequiredArgsConstructor
@Slf4j
public class MqExceptionLogServiceImpl implements ExceptionLogService {

  private final RabbitTemplate rabbitTemplate;

  /**
   * 发送异常日志.
   *
   * @param message 异常日志消息
   */
  @Override
  public void send(ExceptionLogMessage message) {
    try {
      rabbitTemplate.convertAndSend(
          MqConstants.EXCEPTION_EXCHANGE, MqConstants.EXCEPTION_ROUTING_KEY, message);
    } catch (Exception e) {
      log.error("发送异常日志到 MQ 失败", e);
    }
  }
}
