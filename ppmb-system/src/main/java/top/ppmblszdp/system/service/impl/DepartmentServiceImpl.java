package top.ppmblszdp.system.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.domain.entity.Department;
import top.ppmblszdp.system.repository.DepartmentRepository;
import top.ppmblszdp.system.service.DepartmentService;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional
  public Department createDepartment(Department department) {
    return departmentRepository.save(department);
  }

  @Override
  public Optional<Department> getDepartmentById(Long id) {
    return departmentRepository.findById(id);
  }

  @Override
  public List<Department> getAllDepartments() {
    return departmentRepository.findAll();
  }

  @Override
  @Transactional
  public Department updateDepartment(Long id, Department department) {
    return departmentRepository
        .findById(id)
        .map(
            existing -> {
              existing.setDeptName(department.getDeptName());
              existing.setDeptCode(department.getDeptCode());
              existing.setParentId(department.getParentId());
              existing.setAbbreviation(department.getAbbreviation());
              existing.setEmail(department.getEmail());
              existing.setPhone(department.getPhone());
              existing.setLeaderId(department.getLeaderId());
              existing.setSortNum(department.getSortNum());
              existing.setStatus(department.getStatus());
              return departmentRepository.save(existing);
            })
        .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
  }

  @Override
  @Transactional
  public void deleteDepartment(Long id) {
    departmentRepository.deleteById(id);
  }
}
