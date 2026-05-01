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

/** 基础实体类 (BaseEntity) 包含所有表共有的最基础字段：主键id、租户id、创建人、创建时间。 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 主键 */
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  /** 多租户标识 */
  @Column(name = "tenant_id", nullable = false, updatable = false)
  private Long tenantId;

  /** 创建时间 */
  @CreatedDate
  @Column(name = "create_time", nullable = false, updatable = false)
  private LocalDateTime createTime;

  /** 创建人唯一标识 */
  @CreatedBy
  @Column(name = "create_by", nullable = false, updatable = false)
  private Long createBy;
}
