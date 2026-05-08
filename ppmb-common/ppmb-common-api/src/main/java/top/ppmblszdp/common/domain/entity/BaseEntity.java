package top.ppmblszdp.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.ppmblszdp.common.tenant.GlobalTable;
import top.ppmblszdp.common.tenant.TenantContextHolder;

/**
 * Base entity with common fields: id, tenant_id, create_by, create_time, dept_id, role_id,
 * data_scope.
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = Long.class)})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Primary key. */
  @Id
  @top.ppmblszdp.common.domain.generator.SnowflakeId
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  /** Multi-tenant identifier. */
  @Column(name = "tenant_id", nullable = false, updatable = false)
  private Long tenantId = 0L;

  /** Creation timestamp. */
  @CreatedDate
  @Column(name = "create_time", nullable = false, updatable = false)
  private LocalDateTime createTime;

  /** Creator user identifier. */
  @CreatedBy
  @Column(name = "create_by", nullable = false, updatable = false)
  private Long createBy;

  /** Department identifier for data permission. Default 0 (virtual department). */
  @Column(name = "dept_id", nullable = false)
  private Long deptId = 0L;

  /** Role identifier for data permission. Default 0 (virtual role). */
  @Column(name = "role_id", nullable = false)
  private Long roleId = 0L;

  /** Data scope level (0: default, 1: read-only, 99: hidden). */
  @Column(name = "data_scope", nullable = false)
  private Integer dataScope = 0;

  /**
   * Sets the ID for testing or internal framework use. Protected to discourage direct use in
   * application logic.
   */
  protected void setId(Long id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void populateTenantId() {
    if (!this.getClass().isAnnotationPresent(GlobalTable.class)) {
      Long currentTenantId = TenantContextHolder.get();
      if (currentTenantId != null) {
        this.tenantId = currentTenantId;
      }
    }
  }
}
