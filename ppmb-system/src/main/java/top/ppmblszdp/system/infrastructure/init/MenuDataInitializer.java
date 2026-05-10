package top.ppmblszdp.system.infrastructure.init;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

/** 菜单与基础权限数据初始化器（异步执行，不阻塞启动）. */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuDataInitializer {

  private static final String ADMIN_USERNAME = "admin";

  private final MenuRepository menuRepository;
  private final RoleRepository roleRepository;
  private final RoleMenuRepository roleMenuRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${ppmb.admin.init.password:Ppmb@2026}")
  private String adminPassword;

  @Async
  @EventListener(ApplicationReadyEvent.class)
  @Transactional(rollbackFor = Exception.class)
  public void initializeMenuData() {
    if (menuRepository.findAll().isEmpty()) {
      log.info("Initializing default menu and role data...");

      List<SysMenu> menus = new ArrayList<>();

      // 1. 系统管理
      SysMenu systemMenu =
          createMenu(
              new MenuDefinition("系统管理", 0L, 1, "system", "M", "SettingOutlined", null, "Layout"));
      systemMenu = menuRepository.save(systemMenu);
      menus.add(systemMenu);

      // 2. 子菜单
      menus.add(
          menuRepository.save(
              createMenu(
                  new MenuDefinition(
                      "用户管理",
                      systemMenu.getId(),
                      1,
                      "user",
                      "C",
                      "UserOutlined",
                      "sys:user:list",
                      "system/user/index"))));
      menus.add(
          menuRepository.save(
              createMenu(
                  new MenuDefinition(
                      "角色管理",
                      systemMenu.getId(),
                      2,
                      "role",
                      "C",
                      "TeamOutlined",
                      "sys:role:list",
                      "system/role/index"))));
      menus.add(
          menuRepository.save(
              createMenu(
                  new MenuDefinition(
                      "菜单管理",
                      systemMenu.getId(),
                      3,
                      "menu",
                      "C",
                      "MenuOutlined",
                      "sys:menu:list",
                      "system/menu/index"))));
      menus.add(
          menuRepository.save(
              createMenu(
                  new MenuDefinition(
                      "审计日志",
                      systemMenu.getId(),
                      4,
                      "audit",
                      "C",
                      "FileSearchOutlined",
                      "sys:audit:list",
                      "system/audit/index"))));

      // 3. 创建超级管理员角色
      if (roleRepository.findAll().isEmpty()) {
        Role adminRole = Role.create("超级管理员", ADMIN_USERNAME, "系统内置超级管理员角色");
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

        // 5. 初始化管理员用户
        if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()) {
          User adminUser =
              User.create(ADMIN_USERNAME, passwordEncoder.encode(adminPassword), "超级管理员");
          adminUser.updateInfo("超级管理员", "admin@ppmb.com", "13800138000");
          adminUser = userRepository.save(adminUser);

          // 6. 关联用户与角色
          userRoleRepository.saveAll(
              List.of(UserRole.create(adminUser.getId(), adminRole.getId())));
          log.info("Admin user '{}' created and linked to Admin role.", ADMIN_USERNAME);
        }
      }

      log.info("Default menu data initialized.");
    }
  }

  private SysMenu createMenu(MenuDefinition definition) {
    SysMenu menu = new SysMenu();
    menu.setMenuName(definition.name());
    menu.setParentId(definition.parentId());
    menu.setOrderNum(definition.order());
    menu.setPath(definition.path());
    menu.setMenuType(definition.type());
    menu.setIcon(definition.icon());
    menu.setPerms(definition.perms());
    menu.setComponent(definition.component());
    menu.setVisible(true);
    return menu;
  }

  private record MenuDefinition(
      String name,
      Long parentId,
      Integer order,
      String path,
      String type,
      String icon,
      String perms,
      String component) {}
}
