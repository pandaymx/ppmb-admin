package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@DisplayName("菜单对象映射器测试")
class MenuAssemblerTest {

  private final MenuAssembler assembler = Mappers.getMapper(MenuAssembler.class);

  @Test
  @DisplayName("测试 Command 转换为 Entity")
  void testToEntity() {
    CreateMenuCommand cmd =
        new CreateMenuCommand("测试", 0L, "M", "/test", "Test", "test:list", "icon", 1, true);
    SysMenu entity = assembler.toEntity(cmd);

    assertNotNull(entity);
    assertEquals("测试", entity.getMenuName());
    assertEquals(0L, entity.getParentId());
  }

  @Test
  @DisplayName("测试 Entity 转换为 DTO")
  void testToDto() {
    SysMenu entity = new SysMenu();
    top.ppmblszdp.common.domain.entity.EntityTestUtils.setId(entity, 1L);
    entity.setMenuName("测试");

    MenuDto dto = assembler.toDto(entity);

    assertNotNull(dto);
    assertEquals(1L, dto.id());
    assertEquals("测试", dto.menuName());
  }

  @Test
  @DisplayName("测试更新 Entity")
  void testUpdateEntity() {
    SysMenu entity = new SysMenu();
    entity.setMenuName("旧名称");

    UpdateMenuCommand cmd =
        new UpdateMenuCommand("新名称", 0L, "M", "/test", "Test", "test:list", "icon", 1, true);
    assembler.updateEntity(entity, cmd);

    assertEquals("新名称", entity.getMenuName());
  }
}
