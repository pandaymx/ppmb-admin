package top.ppmblszdp.system.domain.model.dept.repository;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.system.domain.model.dept.entity.Department;

/** 部门仓储接口. */
public interface DepartmentRepository {

  Department save(Department department);

  Optional<Department> findById(Long id);

  List<Department> findAll();

  void deleteById(Long id);
}
