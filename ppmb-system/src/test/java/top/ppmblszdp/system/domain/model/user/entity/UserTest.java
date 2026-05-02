package top.ppmblszdp.system.domain.model.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("用户领域实体测试")
class UserTest {

  @Test
  @DisplayName("创建用户成功")
  void createUserSuccess() {
    User user = User.create("testuser", "password123", "Tester");
    assertNotNull(user);
    assertEquals("testuser", user.getUsername());
    assertEquals("password123", user.getPassword());
    assertEquals("Tester", user.getNickname());
    assertEquals(0, user.getStatus());
  }

  @Test
  @DisplayName("创建用户失败-用户名为空")
  void createUserFailUsernameEmpty() {
    assertThrows(BusinessException.class, () -> User.create("", "password123", "Tester"));
  }

  @Test
  @DisplayName("创建用户失败-密码为空")
  void createUserFailPasswordEmpty() {
    assertThrows(BusinessException.class, () -> User.create("testuser", null, "Tester"));
  }

  @Test
  @DisplayName("更新用户信息成功")
  void updateUserInfo() {
    User user = User.create("testuser", "password123", "Tester");
    user.updateInfo("New Nickname", "test@example.com", "123456789");
    assertEquals("New Nickname", user.getNickname());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("123456789", user.getPhone());
  }

  @Test
  @DisplayName("禁用和启用用户成功")
  void disableAndEnableUser() {
    User user = User.create("testuser", "password123", "Tester");
    user.disable();
    assertEquals(1, user.getStatus());
    user.enable();
    assertEquals(0, user.getStatus());
  }
}
