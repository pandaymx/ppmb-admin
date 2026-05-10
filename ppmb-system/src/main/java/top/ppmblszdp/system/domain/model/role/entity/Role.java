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

/** 角色领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_role")
public class Role extends BaseMainEntity {

  @Column(name = "role_name", nullable = false, length = 100)
  private String roleName;

  @Column(name = "role_code", nullable = false, unique = true, length = 100)
  private String roleCode;

  @Column(name = "description")
  private String description;

  @Column(name = "status", nullable = false)
  private Integer status;

  @Column(name = "is_readonly", nullable = false)
  private Boolean isReadonly;

  // 使用父类 BaseEntity 中的 dataScope，不需要重新定义字段，只需要提供 setter
  public void setDataScopeValue(Integer dataScope) {
    if (dataScope != null) {
      super.setDataScope(dataScope);
    }
  }

  /**
   * 创建角色.
   *
   * @param roleName 角色名称
   * @param roleCode 角色编码
   * @param description 描述
   * @return 角色实体
   */
  public static Role create(String roleName, String roleCode, String description) {
    AssertUtils.notEmpty(roleName, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(roleCode, CommonResultCode.PARAM_ERROR);
    Role role = new Role();
    role.setRoleName(roleName);
    role.setRoleCode(roleCode);
    role.setDescription(description);
    role.setStatus(1); // 默认启用
    role.setIsReadonly(false); // 默认不是内置只读角色
    role.setDataScope(1); // 默认全部数据权限
    return role;
  }

  /**
   * 更新基本信息.
   *
   * @param roleName 角色名称
   * @param description 描述
   */
  public void updateInfo(String roleName, String description) {
    AssertUtils.notEmpty(roleName, CommonResultCode.PARAM_ERROR);
    this.roleName = roleName;
    this.description = description;
  }

  /** 禁用角色. */
  public void disable() {
    this.status = 0;
  }

  /** 启用角色. */
  public void enable() {
    this.status = 1;
  }
}
