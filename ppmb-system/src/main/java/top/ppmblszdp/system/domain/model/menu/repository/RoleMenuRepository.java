package top.ppmblszdp.system.domain.model.menu.repository;

import java.util.List;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;

public interface RoleMenuRepository {
  List<SysRoleMenu> findByRoleId(Long roleId);

  List<SysRoleMenu> findByRoleIds(List<Long> roleIds);

  void deleteByRoleId(Long roleId);

  void saveAll(List<SysRoleMenu> roleMenus);

  List<Long> findRoleIdsByMenuPerm(String permission);
}
