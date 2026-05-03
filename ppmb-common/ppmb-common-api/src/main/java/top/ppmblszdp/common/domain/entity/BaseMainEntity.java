package top.ppmblszdp.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/** Main table entity extending BaseEntity with update, soft-delete and optimistic lock fields. */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class BaseMainEntity extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** Department of the creator. */
  @Column(name = "create_by_dept", updatable = false)
  private Long createByDept;

  /** Last modified by user identifier. */
  @LastModifiedBy
  @Column(name = "update_by")
  private Long updateBy;

  /** Last modification timestamp. */
  @LastModifiedDate
  @Column(name = "update_time")
  private LocalDateTime updateTime;

  /** Logical deletion flag (0 = not deleted). */
  @Column(name = "del_flag", nullable = false)
  private Integer delFlag;

  /** Optimistic lock version number. */
  @Version
  @Column(name = "version", nullable = false)
  private Integer version;
}
