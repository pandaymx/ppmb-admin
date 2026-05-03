package top.ppmblszdp.system.domain.model.menu.repository;

import java.util.List;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;

public interface RoleMenuRepository {
  <S extends SysRoleMenu> List<S> saveAll(Iterable<S> roleMenus);

  void deleteByRoleId(Long roleId);

  List<SysRoleMenu> findByRoleId(Long roleId);

  List<SysRoleMenu> findByRoleIdIn(List<Long> roleIds);
}
