package top.ppmblszdp.system.domain.model.menu.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("角色菜单关联领域实体测试")
class SysRoleMenuTest {

  @Test
  @DisplayName("测试角色菜单关联属性设置与读取")
  void testRoleMenuProperties() {
    SysRoleMenu roleMenu = new SysRoleMenu();
    roleMenu.setRoleId(1L);
    roleMenu.setMenuId(2L);

    assertEquals(1L, roleMenu.getRoleId());
    assertEquals(2L, roleMenu.getMenuId());
  }
}
