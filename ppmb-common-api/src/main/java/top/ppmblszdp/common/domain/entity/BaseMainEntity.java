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

/** 主表实体类 (BaseMainEntity) 继承 BaseEntity，增加了修改人、修改时间、创建人部门、逻辑删除标记和乐观锁版本号等针对主表的常见字段。 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class BaseMainEntity extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 创建人所属部门 */
  @Column(name = "create_by_dept", updatable = false)
  private Long createByDept;

  /** 最后修改人 */
  @LastModifiedBy
  @Column(name = "update_by")
  private Long updateBy;

  /** 修改时间 */
  @LastModifiedDate
  @Column(name = "update_time")
  private LocalDateTime updateTime;

  /** 逻辑删除标记 (默认 0 表示未删除) */
  @Column(name = "del_flag", nullable = false)
  private Integer delFlag;

  /** 乐观锁版本号 */
  @Version
  @Column(name = "version", nullable = false)
  private Integer version;
}
