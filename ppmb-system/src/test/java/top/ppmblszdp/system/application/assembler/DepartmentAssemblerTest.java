package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.system.domain.model.dept.entity.Department;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDto;

@DisplayName("部门对象映射器测试")
class DepartmentAssemblerTest {

  private final DepartmentAssembler assembler = Mappers.getMapper(DepartmentAssembler.class);

  @Test
  @DisplayName("测试 Entity 转换为 DTO")
  void testToDto() {
    Department dept = Department.create("研发部", "RD", 0L, 1);
    dept.update("研发中心", "RDC", "研发", "rd@test.com", "123", 1L, 2);

    DepartmentDto dto = assembler.toDto(dept);

    assertNotNull(dto);
    assertEquals("研发中心", dto.deptName());
    assertEquals("RDC", dto.deptCode());
    assertEquals("研发", dto.abbreviation());
    assertEquals("rd@test.com", dto.email());
    assertEquals("123", dto.phone());
    assertEquals(1L, dto.leaderId());
    assertEquals(2, dto.sortNum());
    assertEquals(0, dto.status());
  }

  @Test
  @DisplayName("测试 Entity 列表转换为 DTO 列表")
  void testToDtoList() {
    Department dept1 = Department.create("部门1", "D1", 0L, 1);
    Department dept2 = Department.create("部门2", "D2", 0L, 2);

    List<DepartmentDto> dtoList = assembler.toDtoList(List.of(dept1, dept2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
    assertEquals("部门1", dtoList.get(0).deptName());
    assertEquals("部门2", dtoList.get(1).deptName());
  }

  @Test
  @DisplayName("测试 Null 值转换")
  void testNull() {
    assertNull(assembler.toDto(null));
    assertNull(assembler.toDtoList(null));
  }
}
