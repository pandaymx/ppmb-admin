package top.ppmblszdp.system.interfaces.web.role.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("用户角色映射 DTO 测试")
class UserRoleDtoTest {

  @Test
  @DisplayName("测试 Record 构造与属性读取")
  void testRecord() {
    Long userId = 1L;
    List<Long> roleIds = List.of(2L, 3L);
    UserRoleDto dto = new UserRoleDto(userId, roleIds);

    assertEquals(userId, dto.userId());
    assertEquals(roleIds, dto.roleIds());
  }
}
