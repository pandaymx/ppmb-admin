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
    when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(List.of());
    List<MenuDto> list = service.getMenuList();
    assertTrue(list.isEmpty());
  }

  @Test
  @DisplayName("获取路由-空路径分支")
  void testGetRouters_EmptyPath() {
    menu.setPath(null); // Null path should lead to empty route name
    menu.setOrderNum(1);
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(2L);
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
    when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(List.of(menu));
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

    when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(List.of(menu, child));
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
    menu.setOrderNum(1);
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of(menu));

    List<RouterVo> routers = service.getRouters(2L);
    assertEquals(1, routers.size());
    assertEquals("System", routers.get(0).getName());
  }

  @Test
  @DisplayName("获取菜单权限列表")
  void testGetMenuPermsByUserId() {
    menu.setPerms("sys:user:list");
    menu.setOrderNum(1);
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of(menu));

    List<String> perms = service.getMenuPermsByUserId(2L);
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

    menu.setPerms("sys:user:list");
    menu.setOrderNum(1);
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of(menu, menu2));

    List<String> perms = service.getMenuPermsByUserId(2L);
    assertEquals(1, perms.size());
    assertEquals("sys:user:list", perms.get(0));
  }

  @Test
  @DisplayName("获取路由-无角色/菜单")
  void testGetRouters_NoRolesOrMenus() {
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of());
    List<RouterVo> routers = service.getRouters(2L);
    assertTrue(routers.isEmpty());
  }

  @Test
  @DisplayName("获取路由-单级父菜单")
  void testGetRouters_SingleLevelParent() {
    when(menuRepository.findByUserIdWithJoin(2L)).thenReturn(List.of(menu));

    menu.setParentId(0L);
    menu.setMenuType("M");
    menu.setOrderNum(1);

    List<RouterVo> routers = service.getRouters(2L);
    assertEquals(1, routers.size());
    assertEquals("Layout", routers.get(0).getComponent());
    assertEquals(1, routers.get(0).getChildren().size());
  }

  @Test
  @DisplayName("超级管理员获取全量路由")
  void testGetRouters_SuperAdmin() {
    when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(List.of(menu));
    List<RouterVo> routers = service.getRouters(1L);
    assertEquals(1, routers.size());
    verify(menuRepository).findAllByOrderByOrderNumAsc();
  }
}
