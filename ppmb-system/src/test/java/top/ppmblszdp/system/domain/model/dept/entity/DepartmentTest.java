package top.ppmblszdp.system.domain.model.dept.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("部门领域实体测试")
class DepartmentTest {

  @Test
  @DisplayName("创建部门成功")
  void createDepartmentSuccess() {
    Department dept = Department.create("IT Department", "IT01", 0L, 1);
    assertNotNull(dept);
    assertEquals("IT Department", dept.getDeptName());
    assertEquals("IT01", dept.getDeptCode());
    assertEquals(0L, dept.getParentId());
    assertEquals(1, dept.getSortNum());
    assertEquals(0, dept.getStatus());
  }

  @Test
  @DisplayName("创建部门失败-名称为空")
  void createDepartmentFailNameEmpty() {
    assertThrows(BusinessException.class, () -> Department.create("", "IT01", 0L, 1));
  }

  @Test
  @DisplayName("修改部门信息成功")
  void updateDepartment() {
    Department dept = Department.create("IT Department", "IT01", 0L, 1);
    dept.update("New Dept", "NEW01", "ND", "nd@example.com", "123456", 1L, 2);
    assertEquals("New Dept", dept.getDeptName());
    assertEquals("NEW01", dept.getDeptCode());
    assertEquals("ND", dept.getAbbreviation());
    assertEquals("nd@example.com", dept.getEmail());
    assertEquals("123456", dept.getPhone());
    assertEquals(1L, dept.getLeaderId());
    assertEquals(2, dept.getSortNum());
  }

  @Test
  @DisplayName("禁用和启用部门成功")
  void disableAndEnableDepartment() {
    Department dept = Department.create("IT Department", "IT01", 0L, 1);
    dept.disable();
    assertEquals(1, dept.getStatus());
    dept.enable();
    assertEquals(0, dept.getStatus());
  }
}
