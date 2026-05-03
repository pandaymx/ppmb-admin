package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;

@DisplayName("角色对象映射器测试")
class RoleAssemblerTest {

  private final RoleAssembler assembler = Mappers.getMapper(RoleAssembler.class);

  @Test
  @DisplayName("测试 Entity 转换为 DTO")
  void testToDto() {
    Role role = Role.create("管理员", "admin", "超级管理员");
    RoleDto dto = assembler.toDto(role);

    assertNotNull(dto);
    assertEquals("管理员", dto.roleName());
    assertEquals("admin", dto.roleCode());
    assertEquals("超级管理员", dto.description());
    assertEquals(1, dto.status());
    assertFalse(dto.isReadonly());
  }

  @Test
  @DisplayName("测试 Entity 列表转换为 DTO 列表")
  void testToDtoList() {
    Role role1 = Role.create("角色1", "r1", null);
    Role role2 = Role.create("角色2", "r2", null);

    List<RoleDto> dtoList = assembler.toDtoList(List.of(role1, role2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
  }

  @Test
  @DisplayName("测试 Null 值转换")
  void testNull() {
    assertNull(assembler.toDto(null));
    assertNull(assembler.toDtoList(null));
  }
}
