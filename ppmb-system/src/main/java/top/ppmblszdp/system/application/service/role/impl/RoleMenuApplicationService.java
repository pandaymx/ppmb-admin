package top.ppmblszdp.system.application.service.role.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;

@Service
@RequiredArgsConstructor
public class RoleMenuApplicationService {

  private final RoleMenuRepository roleMenuRepository;

  @Transactional(rollbackFor = Exception.class)
  public void assignMenusToRole(Long roleId, List<Long> menuIds) {
    roleMenuRepository.deleteByRoleId(roleId);

    if (menuIds != null && !menuIds.isEmpty()) {
      List<SysRoleMenu> roleMenus =
          menuIds.stream()
              .map(
                  menuId -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                  })
              .collect(Collectors.toList());

      roleMenuRepository.saveAll(roleMenus);
    }
  }
}
