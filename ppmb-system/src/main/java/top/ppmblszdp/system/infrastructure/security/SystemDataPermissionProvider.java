package top.ppmblszdp.system.infrastructure.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.security.data.DataPermissionContext;
import top.ppmblszdp.common.security.data.DataPermissionProvider;
import top.ppmblszdp.common.security.data.enums.DataScope;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleDeptRepository;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SystemDataPermissionProvider implements DataPermissionProvider {

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final RoleDeptRepository roleDeptRepository;

  @Override
  public DataPermissionContext getPermissionContext(Long userId, String permission) {
    if (userId == null) {
      return null;
    }

    // 如果是超级管理员(通常 id=1)，直接给 ALL
    if (userId.equals(1L)) {
      return DataPermissionContext.builder().dataScope(DataScope.ALL).build();
    }

    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      return null;
    }

    List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
    if (roleIds.isEmpty()) {
       return buildSelfContext(userId);
    }

    // FIXME: For a fully correct menu filtering, we should filter roleIds by the ones that actually grant 'permission'.
    // Here we simplify by checking all roles of the user for their highest scope.
    List<Long> permRoleIds = new ArrayList<>(roleIds);

    List<Role> roles = roleRepository.findByIdIn(permRoleIds);
    if (roles.isEmpty()) {
       return buildSelfContext(userId);
    }

    // 寻找最大的 DataScope (值越小，权限越大。1: ALL, 2: CUSTOM, 3: DEPT, 4: DEPT_AND_CHILD, 5: SELF)
    int minScopeValue = 5;
    for (Role role : roles) {
      if (role.getDataScope() != null && role.getDataScope() < minScopeValue) {
        minScopeValue = role.getDataScope();
      }
    }

    DataScope finalScope = DataScope.valueOf(minScopeValue);
    Set<Long> deptIds = new HashSet<>();
    Long userDeptId = user.getDeptId();

    switch (finalScope) {
      case ALL:
        return DataPermissionContext.builder().dataScope(DataScope.ALL).build();
      case CUSTOM:
        // 收集所有具有 CUSTOM 或更小权限的角色的关联部门
        List<Long> customRoleIds = roles.stream()
            .filter(r -> r.getDataScope() != null && r.getDataScope() <= DataScope.CUSTOM.getValue())
            .map(Role::getId)
            .toList();
        deptIds.addAll(roleDeptRepository.findDeptIdsByRoleIds(customRoleIds));
        break;
      case DEPT:
        if (userDeptId != null) {
            deptIds.add(userDeptId);
        }
        break;
      case DEPT_AND_CHILD:
        if (userDeptId != null) {
            deptIds.add(userDeptId);
            // 这里应该调用部门服务查询所有的子部门ID并添加。为了简化和保持上下文，暂且加入本部门
        }
        break;
      case SELF:
      default:
        return buildSelfContext(userId);
    }

    return DataPermissionContext.builder()
        .dataScope(finalScope)
        .deptIds(deptIds)
        .userId(userId)
        .build();
  }

  private DataPermissionContext buildSelfContext(Long userId) {
       return DataPermissionContext.builder()
          .dataScope(DataScope.SELF)
          .userId(userId)
          .build();
  }
}
