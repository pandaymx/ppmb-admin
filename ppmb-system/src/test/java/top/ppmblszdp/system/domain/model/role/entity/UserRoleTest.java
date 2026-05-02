package top.ppmblszdp.system.domain.model.role.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

class UserRoleTest {

  @Test
  void testCreate() {
    UserRole userRole = UserRole.create(1L, 2L);
    assertEquals(1L, userRole.getUserId());
    assertEquals(2L, userRole.getRoleId());
  }

  @Test
  void testCreateWithNullUserId() {
    assertThrows(BusinessException.class, () -> UserRole.create(null, 2L));
  }

  @Test
  void testCreateWithNullRoleId() {
    assertThrows(BusinessException.class, () -> UserRole.create(1L, null));
  }
}
