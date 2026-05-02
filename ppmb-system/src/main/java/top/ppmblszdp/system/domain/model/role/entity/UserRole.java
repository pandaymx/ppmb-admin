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
import top.ppmblszdp.common.domain.entity.BaseEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 用户角色关联实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_user_role")
public class UserRole extends BaseEntity {

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  /**
   * 创建用户角色关联.
   *
   * @param userId 用户 ID
   * @param roleId 角色 ID
   * @return 用户角色关联实体
   */
  public static UserRole create(Long userId, Long roleId) {
    AssertUtils.notNull(userId, CommonResultCode.PARAM_ERROR);
    AssertUtils.notNull(roleId, CommonResultCode.PARAM_ERROR);
    UserRole userRole = new UserRole();
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    return userRole;
  }
}
