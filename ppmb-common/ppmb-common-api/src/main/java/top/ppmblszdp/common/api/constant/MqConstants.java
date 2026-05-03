package top.ppmblszdp.common.api.constant;

/** MQ 业务常量. */
public final class MqConstants {
  private MqConstants() {}

  /** 异常日志交换机. */
  public static final String EXCEPTION_EXCHANGE = "sys.exception.exchange";

  /** 异常日志队列. */
  public static final String EXCEPTION_QUEUE = "sys.exception.queue";

  /** 异常日志路由键. */
  public static final String EXCEPTION_ROUTING_KEY = "sys.exception.log";
}
