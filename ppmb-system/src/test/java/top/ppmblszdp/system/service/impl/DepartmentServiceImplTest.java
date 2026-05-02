package top.ppmblszdp.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.entity.Department;
import top.ppmblszdp.system.repository.DepartmentRepository;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

  @Mock private DepartmentRepository departmentRepository;

  @InjectMocks private DepartmentServiceImpl departmentService;

  private Department department;

  @BeforeEach
  void setUp() {
    department = new Department();
    department.setId(1L);
    department.setDeptName("HR Department");
    department.setStatus(0);
  }

  @Test
  void createDepartment() {
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Department result = departmentService.createDepartment(new Department());

    assertEquals(1L, result.getId());
    assertEquals("HR Department", result.getDeptName());
    verify(departmentRepository, times(1)).save(any(Department.class));
  }

  @Test
  void getDepartmentById() {
    when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

    Optional<Department> result = departmentService.getDepartmentById(1L);

    assertTrue(result.isPresent());
    assertEquals("HR Department", result.get().getDeptName());
  }

  @Test
  void getAllDepartments() {
    when(departmentRepository.findAll()).thenReturn(Arrays.asList(department));

    List<Department> result = departmentService.getAllDepartments();

    assertEquals(1, result.size());
    assertEquals("HR Department", result.get(0).getDeptName());
  }

  @Test
  void updateDepartment() {
    when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Department updated = new Department();
    updated.setDeptName("Updated HR");

    Department result = departmentService.updateDepartment(1L, updated);

    assertEquals("Updated HR", result.getDeptName());
    verify(departmentRepository, times(1)).save(any(Department.class));
  }

  @Test
  void updateDepartmentNotFound() {
    when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

    Department dept = new Department();
    assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(1L, dept));
  }

  @Test
  void deleteDepartment() {
    doNothing().when(departmentRepository).deleteById(1L);

    departmentService.deleteDepartment(1L);

    verify(departmentRepository, times(1)).deleteById(1L);
  }
}
