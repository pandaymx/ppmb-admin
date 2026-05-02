package top.ppmblszdp.system.application.service.dept;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDto;

/** 部门应用服务接口. */
public interface DepartmentApplicationService {

  DepartmentDto createDepartment(DepartmentDto departmentDto);

  Optional<DepartmentDto> getDepartmentById(Long id);

  List<DepartmentDto> getAllDepartments();

  DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto);

  void deleteDepartment(Long id);
}
