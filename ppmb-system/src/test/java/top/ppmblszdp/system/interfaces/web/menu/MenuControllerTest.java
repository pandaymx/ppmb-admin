package top.ppmblszdp.system.interfaces.web.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.RouterVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuController 接口测试")
class MenuControllerTest {

  @Mock private MenuApplicationService menuApplicationService;

  @InjectMocks private MenuController controller;

  @Test
  @DisplayName("创建菜单")
  void testCreateMenu() {
    CreateMenuCommand command =
        new CreateMenuCommand("Test", 0L, "M", "/path", "Comp", "perm", "icon", 1, true);
    MenuDto dto =
        new MenuDto(
            1L, "Test", 0L, "M", "/path", "Comp", "perm", "icon", 1, true, null, List.of());

    when(menuApplicationService.createMenu(any())).thenReturn(dto);

    Result<MenuDto> result = controller.createMenu(command);
    assertEquals("00000", result.code());
    assertEquals("Test", result.data().menuName());
  }

  @Test
  @DisplayName("更新菜单")
  void testUpdateMenu() {
    UpdateMenuCommand command =
        new UpdateMenuCommand("Update", 0L, "M", "/path", "Comp", "perm", "icon", 1, true);
    MenuDto dto =
        new MenuDto(
            1L, "Update", 0L, "M", "/path", "Comp", "perm", "icon", 1, true, null, List.of());

    when(menuApplicationService.updateMenu(anyLong(), any())).thenReturn(dto);

    Result<MenuDto> result = controller.updateMenu(1L, command);
    assertEquals("00000", result.code());
    assertEquals("Update", result.data().menuName());
  }

  @Test
  @DisplayName("删除菜单")
  void testDeleteMenu() {
    Result<Void> result = controller.deleteMenu(1L);
    verify(menuApplicationService, times(1)).deleteMenu(1L);
    assertEquals("00000", result.code());
  }

  @Test
  @DisplayName("根据 ID 获取菜单")
  void testGetMenuById() {
    MenuDto dto =
        new MenuDto(
            1L, "Detail", 0L, "M", "/path", "Comp", "perm", "icon", 1, true, null, List.of());
    when(menuApplicationService.getMenuById(1L)).thenReturn(dto);

    Result<MenuDto> result = controller.getMenuById(1L);
    assertEquals("00000", result.code());
    assertEquals("Detail", result.data().menuName());
  }

  @Test
  @DisplayName("获取菜单树")
  void testGetMenuTree() {
    MenuDto dto =
        new MenuDto(
            1L, "Tree", 0L, "M", "/path", "Comp", "perm", "icon", 1, true, null, List.of());
    when(menuApplicationService.getMenuTree()).thenReturn(List.of(dto));

    Result<List<MenuDto>> result = controller.getMenuTree();
    assertEquals("00000", result.code());
    assertEquals(1, result.data().size());
  }

  @Test
  @DisplayName("获取路由树")
  void testGetRouters() {
    RouterVo vo = new RouterVo();
    vo.setName("Router");
    when(menuApplicationService.getRouters(anyLong())).thenReturn(List.of(vo));

    Result<List<RouterVo>> result = controller.getRouters();
    assertEquals("00000", result.code());
    assertEquals(1, result.data().size());
  }
}
