package top.ppmblszdp.system.domain.model.dept.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 部门领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_department")
public class Department extends BaseMainEntity {

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "dept_name", nullable = false, length = 100)
  private String deptName;

  @Column(name = "dept_code", length = 50)
  private String deptCode;

  @Column(name = "abbreviation", length = 50)
  private String abbreviation;

  @Column(name = "email", length = 100)
  private String email;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "leader_id")
  private Long leaderId;

  @Column(name = "sort_num")
  private Integer sortNum;

  @Column(name = "status", nullable = false)
  private Integer status;

  /**
   * 创建部门.
   *
   * @param name 部门名称
   * @param code 部门编码
   * @param parentId 父部门 ID
   * @param sortNum 排序号
   * @return 部门实体
   */
  public static Department create(String name, String code, Long parentId, Integer sortNum) {
    AssertUtils.notEmpty(name, CommonResultCode.PARAM_ERROR);
    Department department = new Department();
    department.setDeptName(name);
    department.setDeptCode(code);
    department.setParentId(parentId);
    department.setSortNum(Optional.ofNullable(sortNum).orElse(0));
    department.setStatus(0); // 默认正常
    return department;
  }

  /**
   * 修改部门信息.
   *
   * @param name 部门名称
   * @param code 部门编码
   * @param abbreviation 简称
   * @param email 邮箱
   * @param phone 电话
   * @param leaderId 负责人 ID
   * @param sortNum 排序号
   */
  public void update(
      String name,
      String code,
      String abbreviation,
      String email,
      String phone,
      Long leaderId,
      Integer sortNum) {
    AssertUtils.notEmpty(name, CommonResultCode.PARAM_ERROR);
    this.deptName = name;
    this.deptCode = code;
    this.abbreviation = abbreviation;
    this.email = email;
    this.phone = phone;
    this.leaderId = leaderId;
    this.sortNum = sortNum;
  }

  /** 启用部门. */
  public void enable() {
    this.status = 0;
  }

  /** 禁用部门. */
  public void disable() {
    this.status = 1;
  }
}
