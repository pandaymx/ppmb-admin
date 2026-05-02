package top.ppmblszdp.system.domain.model.role.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

class RoleTest {

  @Test
  void testCreate() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Administrator role");
    assertEquals("Admin", role.getRoleName());
    assertEquals("ROLE_ADMIN", role.getRoleCode());
    assertEquals("Administrator role", role.getDescription());
    assertEquals(1, role.getStatus());
    assertFalse(role.getIsReadonly());
  }

  @Test
  void testCreateWithInvalidName() {
    assertThrows(BusinessException.class, () -> Role.create("", "ROLE_ADMIN", "Desc"));
  }

  @Test
  void testCreateWithInvalidCode() {
    assertThrows(BusinessException.class, () -> Role.create("Admin", "", "Desc"));
  }

  @Test
  void testUpdateInfo() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    role.updateInfo("Super Admin", "Super Desc");
    assertEquals("Super Admin", role.getRoleName());
    assertEquals("Super Desc", role.getDescription());
  }

  @Test
  void testEnableDisable() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    role.disable();
    assertEquals(0, role.getStatus());
    role.enable();
    assertEquals(1, role.getStatus());
  }
}
