package top.ppmblszdp.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;

/** Department entity representing a company department. */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "sys_department")
public class Department extends BaseMainEntity {

  /** Parent department ID. */
  @Column(name = "parent_id")
  private Long parentId;

  /** Department name. */
  @Column(name = "dept_name", nullable = false, length = 100)
  private String deptName;

  /** Department code. */
  @Column(name = "dept_code", length = 50)
  private String deptCode;

  /** Department abbreviation. */
  @Column(name = "abbreviation", length = 50)
  private String abbreviation;

  /** Email. */
  @Column(name = "email", length = 100)
  private String email;

  /** Phone number. */
  @Column(name = "phone", length = 20)
  private String phone;

  /** Department leader user ID. */
  @Column(name = "leader_id")
  private Long leaderId;

  /** Sort order. */
  @Column(name = "sort_num")
  private Integer sortNum;

  /** Status (0 = normal, 1 = disabled). */
  @Column(name = "status", nullable = false)
  private Integer status;
}
