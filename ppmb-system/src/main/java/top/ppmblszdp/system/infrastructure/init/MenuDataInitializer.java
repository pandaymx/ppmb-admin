package top.ppmblszdp.system.infrastructure.init;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;

/** 菜单与基础权限数据初始化器. */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuDataInitializer implements CommandLineRunner {

  private final MenuRepository menuRepository;
  private final RoleRepository roleRepository;
  private final RoleMenuRepository roleMenuRepository;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void run(String... args) {
    if (menuRepository.findAll().isEmpty()) {
      log.info("Initializing default menu and role data...");

      List<SysMenu> menus = new ArrayList<>();

      // 1. 系统管理
      SysMenu systemMenu =
          createMenu("系统管理", 0L, 1, "system", "M", "SettingOutlined", null, "Layout");
      systemMenu = menuRepository.save(systemMenu);
      menus.add(systemMenu);

      // 2. 子菜单
      menus.add(
          menuRepository.save(
              createMenu(
                  "用户管理",
                  systemMenu.getId(),
                  1,
                  "user",
                  "C",
                  "UserOutlined",
                  "sys:user:list",
                  "system/user/index")));
      menus.add(
          menuRepository.save(
              createMenu(
                  "角色管理",
                  systemMenu.getId(),
                  2,
                  "role",
                  "C",
                  "TeamOutlined",
                  "sys:role:list",
                  "system/role/index")));
      menus.add(
          menuRepository.save(
              createMenu(
                  "菜单管理",
                  systemMenu.getId(),
                  3,
                  "menu",
                  "C",
                  "MenuOutlined",
                  "sys:menu:list",
                  "system/menu/index")));

      // 3. 创建超级管理员角色
      if (roleRepository.findAll().isEmpty()) {
        Role adminRole = Role.create("超级管理员", "admin", "系统内置超级管理员角色");
        adminRole = roleRepository.save(adminRole);

        // 4. 关联角色与菜单
        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (SysMenu menu : menus) {
          SysRoleMenu rm = new SysRoleMenu();
          rm.setRoleId(adminRole.getId());
          rm.setMenuId(menu.getId());
          roleMenus.add(rm);
        }
        roleMenuRepository.saveAll(roleMenus);
        log.info("Admin role and role-menu mappings initialized.");
      }

      log.info("Default menu data initialized.");
    }
  }

  private SysMenu createMenu(
      String name,
      Long parentId,
      Integer order,
      String path,
      String type,
      String icon,
      String perms,
      String component) {
    SysMenu menu = new SysMenu();
    menu.setMenuName(name);
    menu.setParentId(parentId);
    menu.setOrderNum(order);
    menu.setPath(path);
    menu.setMenuType(type);
    menu.setIcon(icon);
    menu.setPerms(perms);
    menu.setComponent(component);
    menu.setVisible(true);
    return menu;
  }
}
