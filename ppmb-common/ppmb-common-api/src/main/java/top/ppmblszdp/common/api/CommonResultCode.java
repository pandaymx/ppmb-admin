package top.ppmblszdp.common.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 通用响应码枚举 遵循阿里规约： A 类：客户端错误 B 类：本系统错误 C 类：第三方服务错误. */
@Getter
@RequiredArgsConstructor
public enum CommonResultCode implements IResultCode {
  SUCCESS("00000", "操作成功"),

  // A 类错误：客户端错误
  USER_ERROR("A0001", "用户端错误"),
  PARAM_ERROR("A0400", "请求参数有误"),
  UNAUTHORIZED("A0401", "未授权"),
  FORBIDDEN("A0403", "拒绝访问"),

  // B 类错误：本系统错误
  SYSTEM_ERROR("B0001", "系统执行出错"),
  DATABASE_ERROR("B0300", "数据库异常"),

  // C 类错误：第三方服务错误
  REMOTE_ERROR("C0001", "第三方服务异常"),
  TIMEOUT_ERROR("C0100", "接口调用超时");

  private final String code;
  private final String message;
}
