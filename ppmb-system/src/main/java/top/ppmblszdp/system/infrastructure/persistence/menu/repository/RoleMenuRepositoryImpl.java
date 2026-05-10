package top.ppmblszdp.system.infrastructure.persistence.menu.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;

@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {
  private final RoleMenuJpaRepository jpaRepository;

  @Override
  public List<SysRoleMenu> findByRoleId(Long roleId) {
    return jpaRepository.findByTargetRoleId(roleId);
  }

  @Override
  public List<SysRoleMenu> findByRoleIds(List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findByTargetRoleIdIn(roleIds);
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    jpaRepository.deleteByTargetRoleId(roleId);
  }

  @Override
  public void saveAll(List<SysRoleMenu> roleMenus) {
    jpaRepository.saveAll(roleMenus);
  }

  @Override
  public List<Long> findRoleIdsByMenuPerm(String permission) {
    return jpaRepository.findRoleIdsByMenuPerms(permission);
  }
}
