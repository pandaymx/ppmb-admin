package top.ppmblszdp.common.api.constant;

/** MQ 业务常量. */
public interface MqConstants {

  /** 异常日志交换机. */
  String EXCEPTION_EXCHANGE = "sys.exception.exchange";

  /** 异常日志队列. */
  String EXCEPTION_QUEUE = "sys.exception.queue";

  /** 异常日志路由键. */
  String EXCEPTION_ROUTING_KEY = "sys.exception.log";
}
