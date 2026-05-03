package top.ppmblszdp.system.application.service.menu.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.assembler.MenuAssembler;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.MetaVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.RouterVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@Service
@RequiredArgsConstructor
public class MenuApplicationServiceImpl implements MenuApplicationService {

  private final MenuRepository menuRepository;
  private final RoleMenuRepository roleMenuRepository;
  private final UserRoleRepository userRoleRepository;
  private final MenuAssembler menuAssembler;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MenuDto createMenu(CreateMenuCommand command) {
    SysMenu menu = menuAssembler.toEntity(command);
    return menuAssembler.toDto(menuRepository.save(menu));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MenuDto updateMenu(Long id, UpdateMenuCommand command) {
    SysMenu menu = menuRepository.findById(id).orElseThrow(() -> new BusinessException("菜单不存在"));
    menuAssembler.updateEntity(menu, command);
    return menuAssembler.toDto(menuRepository.save(menu));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMenu(Long id) {
    menuRepository.deleteById(id);
  }

  @Override
  public MenuDto getMenuById(Long id) {
    SysMenu menu = menuRepository.findById(id).orElseThrow(() -> new BusinessException("菜单不存在"));
    return menuAssembler.toDto(menu);
  }

  @Override
  public List<MenuDto> getMenuList() {
    return menuRepository.findAll().stream()
        .sorted(Comparator.comparingInt(SysMenu::getOrderNum))
        .map(menuAssembler::toDto)
        .toList();
  }

  @Override
  public List<MenuDto> getMenuTree() {
    List<MenuDto> allMenus = getMenuList();
    return buildMenuTree(allMenus, 0L);
  }

  @Override
  public List<RouterVo> getRouters(Long userId) {
    List<SysMenu> userMenus = getMenusByUserId(userId);
    List<SysMenu> treeMenus = buildMenuEntityTree(userMenus, 0L);
    return buildRouters(treeMenus);
  }

  @Override
  public List<String> getMenuPermsByUserId(Long userId) {
    return getMenusByUserId(userId).stream()
        .map(SysMenu::getPerms)
        .filter(StringUtils::hasText)
        .toList();
  }

  private List<SysMenu> getMenusByUserId(Long userId) {
    // Here we could implement admin bypassing
    List<Long> roleIds =
        userRoleRepository.findByUserId(userId).stream().map(UserRole::getRoleId).toList();

    if (roleIds.isEmpty()) {
      return new ArrayList<>();
    }

    List<Long> menuIds =
        roleMenuRepository.findByRoleIdIn(roleIds).stream()
            .map(SysRoleMenu::getMenuId)
            .distinct()
            .toList();

    if (menuIds.isEmpty()) {
      return new ArrayList<>();
    }

    return menuRepository.findByIdIn(menuIds).stream()
        .sorted(Comparator.comparingInt(SysMenu::getOrderNum))
        .toList();
  }

  private List<MenuDto> buildMenuTree(List<MenuDto> menus, Long parentId) {
    List<MenuDto> tree = new ArrayList<>();
    for (MenuDto menu : menus) {
      if (Objects.equals(menu.getParentId(), parentId)) {
        menu.setChildren(buildMenuTree(menus, menu.getId()));
        tree.add(menu);
      }
    }
    return tree;
  }

  private List<SysMenu> buildMenuEntityTree(List<SysMenu> menus, Long parentId) {
    List<SysMenu> tree = new ArrayList<>();
    for (SysMenu menu : menus) {
      if (Objects.equals(menu.getParentId(), parentId)) {
        menu.setChildren(buildMenuEntityTree(menus, menu.getId()));
        tree.add(menu);
      }
    }
    return tree;
  }

  private List<RouterVo> buildRouters(List<SysMenu> menus) {
    List<RouterVo> routers = new ArrayList<>();
    for (SysMenu menu : menus) {
      RouterVo router = new RouterVo();
      router.setHidden(!menu.getVisible());
      router.setName(getRouteName(menu));
      router.setPath(getRouterPath(menu));
      router.setComponent(getComponent(menu));
      router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), false, null));

      List<SysMenu> childMenus = menu.getChildren();
      if (childMenus != null && !childMenus.isEmpty() && "M".equals(menu.getMenuType())) {
        router.setChildren(buildRouters(childMenus));
      } else if ("M".equals(menu.getMenuType()) && menu.getParentId() == 0L) {
        // To allow single level parent route rendering
        RouterVo children = new RouterVo();
        children.setPath("index");
        children.setName(router.getName() + "Index");
        children.setComponent(router.getComponent());
        children.setMeta(router.getMeta());

        router.setComponent("Layout");
        List<RouterVo> childList = new ArrayList<>();
        childList.add(children);
        router.setChildren(childList);
      }

      routers.add(router);
    }
    return routers;
  }

  private String getRouteName(SysMenu menu) {
    String path = menu.getPath();
    if (StringUtils.hasText(path)) {
      return StringUtils.capitalize(path);
    }
    return "";
  }

  private String getRouterPath(SysMenu menu) {
    return menu.getPath();
  }

  private String getComponent(SysMenu menu) {
    if (StringUtils.hasText(menu.getComponent())) {
      return menu.getComponent();
    }
    if (menu.getParentId() == 0L && "M".equals(menu.getMenuType())) {
      return "Layout";
    }
    return "ParentView";
  }
}
