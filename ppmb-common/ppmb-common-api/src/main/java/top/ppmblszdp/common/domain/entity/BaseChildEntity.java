package top.ppmblszdp.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Child table entity extending BaseEntity with parent_id for detail or transaction tables. */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class BaseChildEntity extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** Parent table primary key. */
  @Column(name = "parent_id", nullable = false)
  private Long parentId;
}
