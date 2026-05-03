package top.ppmblszdp.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Base entity with common fields: id, tenant_id, create_by, create_time. */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Primary key. */
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  /** Multi-tenant identifier. */
  @Column(name = "tenant_id", nullable = false, updatable = false)
  private Long tenantId;

  /** Creation timestamp. */
  @CreatedDate
  @Column(name = "create_time", nullable = false, updatable = false)
  private LocalDateTime createTime;

  /** Creator user identifier. */
  @CreatedBy
  @Column(name = "create_by", nullable = false, updatable = false)
  private Long createBy;
}
