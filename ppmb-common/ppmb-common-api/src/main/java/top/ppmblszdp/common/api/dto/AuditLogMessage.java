package top.ppmblszdp.common.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 审计日志消息 DTO. */
public record AuditLogMessage(
    String traceId,
    String operationName,
    String entityName,
    String entityId,
    String oldValue,
    String newValue,
    String requestUri,
    String requestMethod,
    String requestParams,
    String ip,
    Long userId,
    Long tenantId,
    LocalDateTime createTime)
    implements Serializable {}
