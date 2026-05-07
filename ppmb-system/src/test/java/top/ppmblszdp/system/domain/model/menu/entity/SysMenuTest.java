package top.ppmblszdp.system.domain.model.menu.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("菜单实体领域逻辑测试")
class SysMenuTest {

  @Test
  @DisplayName("测试路由名称生成")
  void testGetRouteName() {
    SysMenu menu = new SysMenu();
    menu.setPath("system");
    assertEquals("System", menu.getRouteName());

    menu.setPath("user-profile");
    assertEquals("User-profile", menu.getRouteName());

    menu.setPath("");
    assertEquals("", menu.getRouteName());

    menu.setPath(null);
    assertEquals("", menu.getRouteName());
  }

  @Test
  @DisplayName("测试组件路径选择逻辑")
  void testGetComponentForRouter() {
    SysMenu menu = new SysMenu();
    
    // Explicit component
    menu.setComponent("system/user/index");
    assertEquals("system/user/index", menu.getComponentForRouter());

    // Root directory
    menu.setComponent("");
    menu.setParentId(0L);
    menu.setMenuType("M");
    assertEquals("Layout", menu.getComponentForRouter());

    // Child directory
    menu.setParentId(1L);
    menu.setMenuType("M");
    assertEquals("ParentView", menu.getComponentForRouter());

    // Non-directory root
    menu.setParentId(0L);
    menu.setMenuType("C");
    assertEquals("ParentView", menu.getComponentForRouter());
  }

  @Test
  @DisplayName("测试菜单类型判断")
  void testTypeChecks() {
    SysMenu menu = new SysMenu();
    menu.setMenuType("M");
    menu.setParentId(0L);
    assertTrue(menu.isDirectory());
    assertTrue(menu.isRootDirectory());

    menu.setParentId(1L);
    assertTrue(menu.isDirectory());
    assertFalse(menu.isRootDirectory());

    menu.setMenuType("C");
    assertFalse(menu.isDirectory());
  }
}
