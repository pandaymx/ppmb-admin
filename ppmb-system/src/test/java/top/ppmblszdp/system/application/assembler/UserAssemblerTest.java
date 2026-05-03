package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@DisplayName("用户对象映射器测试")
class UserAssemblerTest {

  private final UserAssembler assembler = Mappers.getMapper(UserAssembler.class);

  @Test
  @DisplayName("测试 Entity 转换为 DTO")
  void testToDto() {
    User user = User.create("admin", "123456", "管理员");
    user.updateInfo("新昵称", "test@test.com", "13800000000");

    UserDto dto = assembler.toDto(user);

    assertNotNull(dto);
    assertEquals("admin", dto.username());
    assertEquals("新昵称", dto.nickname());
    assertEquals("test@test.com", dto.email());
    assertEquals("13800000000", dto.phone());
    assertEquals(0, dto.status());
  }

  @Test
  @DisplayName("测试 Entity 列表转换为 DTO 列表")
  void testToDtoList() {
    User user1 = User.create("user1", "123456", "用户1");
    User user2 = User.create("user2", "123456", "用户2");

    List<UserDto> dtoList = assembler.toDtoList(List.of(user1, user2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
    assertEquals("user1", dtoList.get(0).username());
    assertEquals("user2", dtoList.get(1).username());
  }

  @Test
  @DisplayName("测试 Null 值转换")
  void testNull() {
    assertNull(assembler.toDto(null));
    assertNull(assembler.toDtoList(null));
  }
}
