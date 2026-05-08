package top.ppmblszdp.common.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Record encapsulating an audit log message to be transmitted over MQ.
 */
public record AuditLogMessage(
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
    LocalDateTime createTime
) implements Serializable {}
