package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictDataDto;
import top.ppmblszdp.system.interfaces.web.dict.dto.DictTypeDto;

@DisplayName("字典对象映射器测试")
class DictAssemblerTest {

  private final DictAssembler assembler = Mappers.getMapper(DictAssembler.class);

  @Test
  @DisplayName("测试 DictType 转换为 DTO")
  void testToTypeDto() {
    DictType type = DictType.create("用户性别", "sys_user_sex", "Y", 0, "备注");
    DictTypeDto dto = assembler.toTypeDto(type);

    assertNotNull(dto);
    assertEquals("用户性别", dto.dictName());
    assertEquals("sys_user_sex", dto.dictType());
    assertEquals("Y", dto.systemFlag());
    assertEquals(0, dto.status());
    assertEquals("备注", dto.remark());
  }

  @Test
  @DisplayName("测试 DictType 列表转换")
  void testToTypeDtoList() {
    DictType type1 = DictType.create("类型1", "t1", "N", 0, null);
    DictType type2 = DictType.create("类型2", "t2", "N", 0, null);

    List<DictTypeDto> dtoList = assembler.toTypeDtoList(List.of(type1, type2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
  }

  @Test
  @DisplayName("测试 DictData 转换为 DTO")
  void testToDataDto() {
    DictData data = DictData.create(1L, 1, "男", "0", "sys_user_sex", "Y", "primary", 0, "备注");
    DictDataDto dto = assembler.toDataDto(data);

    assertNotNull(dto);
    assertEquals(1L, dto.parentId());
    assertEquals(1, dto.dictSort());
    assertEquals("男", dto.dictLabel());
    assertEquals("0", dto.dictValue());
    assertEquals("sys_user_sex", dto.dictType());
    assertEquals("Y", dto.isDefault());
    assertEquals("primary", dto.listClass());
    assertEquals(0, dto.status());
    assertEquals("备注", dto.remark());
  }

  @Test
  @DisplayName("测试 DictData 列表转换")
  void testToDataDtoList() {
    DictData data1 = DictData.create(1L, 1, "男", "0", "sys_user_sex", "Y", null, 0, null);
    DictData data2 = DictData.create(1L, 2, "女", "1", "sys_user_sex", "N", null, 0, null);

    List<DictDataDto> dtoList = assembler.toDataDtoList(List.of(data1, data2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
  }

  @Test
  @DisplayName("测试 Null 值转换")
  void testNull() {
    assertNull(assembler.toTypeDto(null));
    assertNull(assembler.toTypeDtoList(null));
    assertNull(assembler.toDataDto(null));
    assertNull(assembler.toDataDtoList(null));
  }
}
