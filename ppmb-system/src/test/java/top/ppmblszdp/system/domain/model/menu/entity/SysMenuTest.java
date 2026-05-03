package top.ppmblszdp.system.domain.model.menu.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("系统菜单领域实体测试")
class SysMenuTest {

  @Test
  @DisplayName("测试菜单基础属性设置与读取")
  void testMenuProperties() {
    SysMenu menu = new SysMenu();
    menu.setMenuName("系统管理");
    menu.setParentId(0L);
    menu.setMenuType("M");
    menu.setPath("/system");
    menu.setComponent("Layout");
    menu.setPerms("system:user:list");
    menu.setIcon("user");
    menu.setOrderNum(1);
    menu.setVisible(true);

    assertEquals("系统管理", menu.getMenuName());
    assertEquals(0L, menu.getParentId());
    assertEquals("M", menu.getMenuType());
    assertEquals("/system", menu.getPath());
    assertEquals("Layout", menu.getComponent());
    assertEquals("system:user:list", menu.getPerms());
    assertEquals("user", menu.getIcon());
    assertEquals(1, menu.getOrderNum());
    assertTrue(menu.getVisible());
    assertNotNull(menu.getChildren());
  }

  @Test
  @DisplayName("测试菜单子节点列表")
  void testMenuChildren() {
    SysMenu parent = new SysMenu();
    SysMenu child = new SysMenu();
    child.setMenuName("用户管理");

    parent.getChildren().add(child);

    assertEquals(1, parent.getChildren().size());
    assertEquals("用户管理", parent.getChildren().get(0).getMenuName());
  }
}
