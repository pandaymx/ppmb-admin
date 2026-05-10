package top.ppmblszdp.system.domain.model.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 角色与部门关联领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_role_dept")
@org.hibernate.annotations.SQLRestriction("del_flag = 0")
@org.hibernate.annotations.SQLDelete(
    sql = "UPDATE sys_role_dept SET del_flag = 1 WHERE id = ? AND version = ?")
public class RoleDept extends BaseMainEntity {

  @Column(name = "target_role_id", nullable = false)
  private Long targetRoleId;

  @Column(name = "target_dept_id", nullable = false)
  private Long targetDeptId;

  /**
   * 创建角色与部门关联.
   *
   * @param roleId 角色ID
   * @param deptId 部门ID
   * @return 关联实体
   */
  public static RoleDept create(Long roleId, Long deptId) {
    AssertUtils.notNull(roleId, CommonResultCode.PARAM_ERROR);
    AssertUtils.notNull(deptId, CommonResultCode.PARAM_ERROR);
    RoleDept roleDept = new RoleDept();
    roleDept.setTargetRoleId(roleId);
    roleDept.setTargetDeptId(deptId);
    return roleDept;
  }
}
