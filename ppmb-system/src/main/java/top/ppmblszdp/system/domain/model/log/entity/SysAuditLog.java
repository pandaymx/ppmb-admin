package top.ppmblszdp.system.domain.model.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import top.ppmblszdp.common.domain.entity.BaseEntity;

/** 审计日志实体. */
@Data
@Entity
@Table(name = "sys_audit_log")
public class SysAuditLog extends BaseEntity {

  @Column(name = "trace_id", unique = true, length = 64)
  private String traceId;

  @Column(name = "operation_name")
  private String operationName;

  @Column(name = "entity_name")
  private String entityName;

  @Column(name = "entity_id")
  private String entityId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "old_value", columnDefinition = "jsonb")
  private String oldValue;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "new_value", columnDefinition = "jsonb")
  private String newValue;

  @Column(name = "request_uri")
  private String requestUri;

  @Column(name = "request_method")
  private String requestMethod;

  @Column(name = "request_params", columnDefinition = "TEXT")
  private String requestParams;

  @Column(name = "ip")
  private String ip;

  @Column(name = "user_id")
  private Long userId;

  @PrePersist
  @Override
  public void populateTenantId() {
    super.populateTenantId();
  }
}
