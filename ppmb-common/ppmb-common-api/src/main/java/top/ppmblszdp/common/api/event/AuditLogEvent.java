package top.ppmblszdp.common.api.event;

import org.springframework.context.ApplicationEvent;
import top.ppmblszdp.common.api.dto.AuditLogMessage;

public class AuditLogEvent extends ApplicationEvent {

    private final AuditLogMessage auditLogMessage;

    public AuditLogEvent(Object source, AuditLogMessage auditLogMessage) {
        super(source);
        this.auditLogMessage = auditLogMessage;
    }

    public AuditLogMessage getAuditLogMessage() {
        return auditLogMessage;
    }
}
