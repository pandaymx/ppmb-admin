package top.ppmblszdp.system.application.service.dept;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDTO;

/** 部门应用服务接口. */
public interface DepartmentApplicationService {

  DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

  Optional<DepartmentDTO> getDepartmentById(Long id);

  List<DepartmentDTO> getAllDepartments();

  DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);

  void deleteDepartment(Long id);
}
