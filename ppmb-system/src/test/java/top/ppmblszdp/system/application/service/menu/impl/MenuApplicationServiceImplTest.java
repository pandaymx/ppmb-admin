package top.ppmblszdp.system.application.service.menu.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    menu.setId(1L);
    menu.setMenuName("System");
    menu.setMenuType("M");
    menu.setPath("system");
    menu.setComponent("Layout");
    menu.setVisible(true);

    dto = new MenuDto();
    dto.setId(1L);
    dto.setMenuName("System");
  }

  @Test
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
  void testUpdateMenu_NotFound() {
    when(menuRepository.findById(1L)).thenReturn(Optional.empty());
    UpdateMenuCommand cmd = new UpdateMenuCommand("Sys", 0L, "M", "", "", "", "", 1, true);
    assertThrows(BusinessException.class, () -> service.updateMenu(1L, cmd));
  }

  @Test
  void testDeleteMenu() {
    service.deleteMenu(1L);
    verify(menuRepository).deleteById(1L);
  }

  @Test
  void testGetMenuById() {
    when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    MenuDto res = service.getMenuById(1L);
    assertNotNull(res);
  }

  @Test
  void testGetMenuList() {
    menu.setOrderNum(1);
    when(menuRepository.findAll()).thenReturn(List.of(menu));
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    List<MenuDto> list = service.getMenuList();
    assertEquals(1, list.size());
  }

  @Test
  void testGetMenuTree() {
    menu.setOrderNum(1);
    when(menuRepository.findAll()).thenReturn(List.of(menu));
    dto.setParentId(0L);
    when(menuAssembler.toDto(menu)).thenReturn(dto);
    List<MenuDto> tree = service.getMenuTree();
    assertEquals(1, tree.size());
  }

  @Test
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
}
