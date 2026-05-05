package top.ppmblszdp.system.application.service.menu.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.assembler.MenuAssembler;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.RouterVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("菜单应用服务实现单元测试")
class MenuApplicationServiceImplTest {

  @Mock private MenuRepository menuRepository;
  @Mock private RoleMenuRepository roleMenuRepository;
  @Mock private UserRoleRepository userRoleRepository;
  @Mock private MenuAssembler menuAssembler;

  @InjectMocks private MenuApplicationServiceImpl service;

  private SysMenu menu;
  private MenuDto dto;

  @BeforeEach
  void setUp() {
    menu = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(menu, 1L);
    menu.setMenuName("System");
    menu.setMenuType("M");
    menu.setPath("system");
    menu.setComponent("Layout");
    menu.setVisible(true);
    menu.setParentId(0L);

    dto = new MenuDto();
    dto.setId(1L);
    dto.setMenuName("System");
    dto.setParentId(0L);
  }

  @Test
  @DisplayName("创建菜单")
  void testCreateMenu() {
    CreateMenuCommand cmd = new CreateMenuCommand("Sys", 0L, "M", "", "", "", "", 1, true);
    when(menuAssembler.toEntity(cmd)).thenReturn(menu);
    when(menuRepository.save(any())).thenReturn(menu);
    when(menuAssembler.toDto(menu)).thenReturn(dto);

    MenuDto res = service.createMenu(cmd);
    assertNotNull(res);
    assertEquals("System", res.getMenuName());
  }

  @Test
  @DisplayName("更新菜单")
  void testUpdateMenu() {
    UpdateMenuCommand cmd = new UpdateMenuCommand("Sys", 0L, "M", "", "", "", "", 1, true);
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuRepository.save(any())).thenReturn(menu);
    when(menuAssembler.toDto(menu)).thenReturn(dto);

    MenuDto res = service.updateMenu(1L, cmd);
    assertNotNull(res);
    verify(menuAssembler).updateEntity(menu, cmd);
  }

  @Test
  @DisplayName("更新菜单失败-不存在")
  void testUpdateMenu_NotFound() {
    when(menuRepository.findById(1L)).thenReturn(Optional.empty());
    UpdateMenuCommand cmd = new UpdateMenuCommand("Sys", 0L, "M", "", "", "", "", 1, true);
    assertThrows(BusinessException.class, () -> service.updateMenu(1L, cmd));
  }

  @Test
  @DisplayName("删除菜单")
  void testDeleteMenu() {
    service.deleteMenu(1L);
    verify(menuRepository).deleteById(1L);
  }

  @Test
  @DisplayName("获取菜单列表-空")
  void testGetMenuList_Empty() {
    when(menuRepository.findAll()).thenReturn(List.of());
    List<MenuDto> list = service.getMenuList();
    assertTrue(list.isEmpty());
  }

  @Test
  @DisplayName("获取路由-空路径分支")
  void testGetRouters_EmptyPath() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    menu.setPath(null); // Null path should lead to empty route name
    menu.setOrderNum(1);
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals("", routers.get(0).getName());
  }

  @Test
  @DisplayName("根据 ID 获取菜单")
  void testGetMenuById() {
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    MenuDto res = service.getMenuById(1L);
    assertNotNull(res);
  }

  @Test
  @DisplayName("获取菜单列表")
  void testGetMenuList() {
    menu.setOrderNum(1);
    when(menuRepository.findAll()).thenReturn(List.of(menu));
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    List<MenuDto> list = service.getMenuList();
    assertEquals(1, list.size());
  }

  @Test
  @DisplayName("获取菜单树")
  void testGetMenuTree() {
    SysMenu child = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(child, 2L);
    child.setParentId(1L);
    child.setOrderNum(1);

    MenuDto childDto = new MenuDto();
    childDto.setId(2L);
    childDto.setParentId(1L);

    when(menuRepository.findAll()).thenReturn(List.of(menu, child));
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    when(menuAssembler.toDto(child)).thenReturn(childDto);

    List<MenuDto> tree = service.getMenuTree();

    assertEquals(1, tree.size());
    assertEquals(1, tree.get(0).getChildren().size());
    assertEquals(2L, tree.get(0).getChildren().get(0).getId());
  }

  @Test
  @DisplayName("获取路由信息")
  void testGetRouters() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    menu.setOrderNum(1);
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    assertEquals("System", routers.get(0).getName());
  }

  @Test
  @DisplayName("获取菜单权限列表")
  void testGetMenuPermsByUserId() {
    menu.setPerms("sys:user:list");
    menu.setOrderNum(1);
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu));

    List<String> perms = service.getMenuPermsByUserId(1L);
    assertEquals(1, perms.size());
    assertEquals("sys:user:list", perms.get(0));
  }

  @Test
  @DisplayName("获取菜单权限列表-包含空权限")
  void testGetMenuPermsByUserId_WithEmpty() {
    SysMenu menu2 = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(menu2, 2L);
    menu2.setPerms(""); // Empty perms
    menu2.setOrderNum(2);

    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm1 = new SysRoleMenu();
    srm1.setMenuId(1L);
    SysRoleMenu srm2 = new SysRoleMenu();
    srm2.setMenuId(2L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm1, srm2));

    menu.setPerms("sys:user:list");
    menu.setOrderNum(1);
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu, menu2));

    List<String> perms = service.getMenuPermsByUserId(1L);
    assertEquals(1, perms.size());
    assertEquals("sys:user:list", perms.get(0));
  }

  @Test
  @DisplayName("获取路由-无角色")
  void testGetRouters_NoRoles() {
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of());
    List<RouterVo> routers = service.getRouters(1L);
    assertTrue(routers.isEmpty());
  }

  @Test
  @DisplayName("获取路由-无菜单")
  void testGetRouters_NoMenus() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of());
    List<RouterVo> routers = service.getRouters(1L);
    assertTrue(routers.isEmpty());
  }

  @Test
  @DisplayName("获取路由-单级父菜单")
  void testGetRouters_SingleLevelParent() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    menu.setParentId(0L);
    menu.setMenuType("M");
    menu.setOrderNum(1);
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    assertEquals("Layout", routers.get(0).getComponent());
    assertEquals(1, routers.get(0).getChildren().size());
  }

  @Test
  @DisplayName("获取路由-空路径和空组件分支")
  void testGetRouters_EmptyPathAndComponent() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    SysMenu menu2 = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(menu2, 2L);
    menu2.setMenuName("User");
    menu2.setMenuType("C");
    menu2.setPath("");
    menu2.setComponent("");
    menu2.setParentId(1L);
    menu2.setOrderNum(1);
    menu2.setVisible(true);

    menu.setChildren(List.of(menu2));
    menu.setOrderNum(1);

    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu, menu2));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    RouterVo childRouter = routers.get(0).getChildren().get(0);
    assertEquals("", childRouter.getName());
    assertEquals("ParentView", childRouter.getComponent());
  }

  @Test
  @DisplayName("获取路由-非目录类型有子菜单不应递归")
  void testGetRouters_NonDirectoryWithChildren() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm = new SysRoleMenu();
    srm.setMenuId(1L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm));

    SysMenu menu2 = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(menu2, 2L);
    menu2.setMenuType("C"); // Menu type, not Directory
    menu2.setPath("user");
    menu2.setParentId(1L);
    menu2.setOrderNum(1);
    menu2.setVisible(true);

    menu.setChildren(List.of(menu2));
    menu.setMenuType("C"); // Parent is also not M
    menu.setOrderNum(1);

    when(menuRepository.findByIdIn(any())).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    assertNull(routers.get(0).getChildren());
  }

  @Test
  @DisplayName("获取路由-各种组件选择逻辑分支")
  void testGetRouters_ComponentBranches() {
    UserRole ur = UserRole.create(1L, 1L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(ur));

    SysRoleMenu srm1 = new SysRoleMenu();
    srm1.setMenuId(1L);
    SysRoleMenu srm2 = new SysRoleMenu();
    srm2.setMenuId(2L);
    when(roleMenuRepository.findByRoleIdIn(any())).thenReturn(List.of(srm1, srm2));

    // Root menu
    SysMenu rootMenu = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(rootMenu, 1L);
    rootMenu.setMenuName("Root");
    rootMenu.setMenuType("M");
    rootMenu.setPath("root");
    rootMenu.setComponent("");
    rootMenu.setParentId(0L);
    rootMenu.setOrderNum(1);
    rootMenu.setVisible(true);

    // Child menu: parentId != 0L 且 component 为空 -> ParentView
    SysMenu childMenu = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(childMenu, 2L);
    childMenu.setMenuName("Child");
    childMenu.setMenuType("M");
    childMenu.setPath("child");
    childMenu.setComponent("");
    childMenu.setParentId(1L);
    childMenu.setOrderNum(1);
    childMenu.setVisible(true);

    rootMenu.setChildren(List.of(childMenu));

    when(menuRepository.findByIdIn(any())).thenReturn(List.of(rootMenu, childMenu));

    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    assertEquals("Layout", routers.get(0).getComponent());
    assertEquals("ParentView", routers.get(0).getChildren().get(0).getComponent());

    // 分支 2: parentId == 0L 但 menuType != "M" 且 component 为空 -> ParentView
    rootMenu.setMenuType("C");
    List<RouterVo> routers2 = service.getRouters(1L);
    assertEquals("ParentView", routers2.get(0).getComponent());

    // 分支 3: childMenus 为 null
    rootMenu.setChildren(null);
    rootMenu.setMenuType("M");
    rootMenu.setParentId(1L); // Trigger no special single-level logic
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(rootMenu));
    List<RouterVo> routers3 = service.getRouters(1L);
    assertTrue(routers3.isEmpty());

    // 分支 4: childMenus 为空列表 (isEmpty) 且为目录类型
    rootMenu.setParentId(0L);
    rootMenu.setChildren(new java.util.ArrayList<>());
    List<RouterVo> routers4 = service.getRouters(1L);
    assertEquals("Layout", routers4.get(0).getComponent());
    assertEquals(1, routers4.get(0).getChildren().size());

    // 分支 5: childMenus 不为空但 menuType 不是 M
    rootMenu.setMenuType("C");
    rootMenu.setChildren(List.of(childMenu));
    List<RouterVo> routers5 = service.getRouters(1L);
    assertNull(routers5.get(0).getChildren());

    // 分支 6: menuType 为 M 但 parentId 不为 0L 且无子菜单
    rootMenu.setMenuType("M");
    rootMenu.setParentId(100L);
    rootMenu.setChildren(null);
    rootMenu.setVisible(true); // Ensure not hidden

    SysMenu dummyRoot = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(dummyRoot, 100L);
    dummyRoot.setMenuName("Dummy");
    dummyRoot.setMenuType("M");
    dummyRoot.setPath("dummy");
    dummyRoot.setVisible(true);
    dummyRoot.setParentId(0L);
    dummyRoot.setChildren(List.of(rootMenu));
    dummyRoot.setOrderNum(1);

    when(roleMenuRepository.findByRoleIdIn(any()))
        .thenReturn(
            List.of(
                srm1,
                srm2,
                new SysRoleMenu() {
                  {
                    setMenuId(100L);
                  }
                }));
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(dummyRoot, rootMenu));

    List<RouterVo> routers6 = service.getRouters(1L);
    assertEquals(1, routers6.size());
    RouterVo childRouter = routers6.get(0).getChildren().get(0);
    assertEquals("ParentView", childRouter.getComponent());
    assertNull(childRouter.getChildren());

    // 分支 7: getRouteName 当 path 为空字符串
    rootMenu.setPath("");
    List<RouterVo> routers7 = service.getRouters(1L);
    assertEquals("", routers7.get(0).getChildren().get(0).getName());

    // 分支 8: childMenus == null, menuType == "M", parentId == 0L
    rootMenu.setParentId(0L);
    rootMenu.setMenuType("M");
    rootMenu.setChildren(null);
    List<RouterVo> routers8 = service.getRouters(1L);
    assertEquals("Layout", routers8.get(0).getComponent());

    // 分支 9: getComponent 当 component 为 null
    rootMenu.setComponent(null);
    List<RouterVo> routers9 = service.getRouters(1L);
    assertEquals("Layout", routers9.get(0).getComponent());

    // 分支 10: getRouteName 当 path 是单个字符
    rootMenu.setPath("a");
    when(menuRepository.findByIdIn(any())).thenReturn(List.of(rootMenu));
    List<RouterVo> routers10 = service.getRouters(1L);
    assertEquals("A", routers10.get(0).getName());
  }
}
