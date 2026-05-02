package top.ppmblszdp.system.application.service.role.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.application.service.role.UserRoleApplicationService;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;

@Service
@RequiredArgsConstructor
public class UserRoleApplicationServiceImpl implements UserRoleApplicationService {

  private final UserRoleRepository userRoleRepository;

  @Override
  public List<Long> getUserRoles(Long userId) {
    return userRoleRepository.findByUserId(userId).stream().map(UserRole::getRoleId).toList();
  }

  @Override
  @Transactional
  public void assignRolesToUser(Long userId, List<Long> roleIds) {
    userRoleRepository.deleteByUserId(userId);
    if (roleIds != null && !roleIds.isEmpty()) {
      List<UserRole> userRoles =
          roleIds.stream().map(roleId -> UserRole.create(userId, roleId)).toList();
      userRoleRepository.saveAll(userRoles);
    }
  }

  @Override
  @Transactional
  public void batchAssignRoles(BatchUserRoleCommand command) {
    List<UserRole> userRoles = new ArrayList<>();
    for (Long userId : command.userIds()) {
      userRoleRepository.deleteByUserId(userId);
      for (Long roleId : command.roleIds()) {
        userRoles.add(UserRole.create(userId, roleId));
      }
    }
    if (!userRoles.isEmpty()) {
      userRoleRepository.saveAll(userRoles);
    }
  }
}
