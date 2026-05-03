package top.ppmblszdp.common.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 异常日志消息 DTO. */
public record ExceptionLogMessage(
    String serviceName,
    String exceptionName,
    String message,
    String stackTrace,
    String requestUri,
    String requestMethod,
    String requestParams,
    String ip,
    Long userId,
    LocalDateTime createTime)
    implements Serializable {}
