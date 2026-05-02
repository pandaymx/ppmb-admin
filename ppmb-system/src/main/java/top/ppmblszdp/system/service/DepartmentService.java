package top.ppmblszdp.system.service;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.system.domain.entity.Department;

public interface DepartmentService {

  Department createDepartment(Department department);

  Optional<Department> getDepartmentById(Long id);

  List<Department> getAllDepartments();

  Department updateDepartment(Long id, Department department);

  void deleteDepartment(Long id);
}
