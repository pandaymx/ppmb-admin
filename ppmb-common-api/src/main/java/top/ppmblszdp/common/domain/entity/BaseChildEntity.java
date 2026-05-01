package top.ppmblszdp.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 子表实体类 (BaseChildEntity) 继承 BaseEntity，增加了父表主键标识，适用于明细表或流水表等子表结构。 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class BaseChildEntity extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 父表主键 */
  @Column(name = "parent_id", nullable = false)
  private Long parentId;
}
