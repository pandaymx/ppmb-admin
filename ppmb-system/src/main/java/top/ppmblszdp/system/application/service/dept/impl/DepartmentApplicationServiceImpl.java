package top.ppmblszdp.system.application.service.dept.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.service.dept.DepartmentApplicationService;
import top.ppmblszdp.system.domain.model.dept.entity.Department;
import top.ppmblszdp.system.domain.model.dept.repository.DepartmentRepository;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDTO;

@Service
@RequiredArgsConstructor
public class DepartmentApplicationServiceImpl implements DepartmentApplicationService {

  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional
  public DepartmentDTO createDepartment(DepartmentDTO dto) {
    Department department =
        Department.create(dto.deptName(), dto.deptCode(), dto.parentId(), dto.sortNum());
    return toDTO(departmentRepository.save(department));
  }

  @Override
  public Optional<DepartmentDTO> getDepartmentById(Long id) {
    return departmentRepository.findById(id).map(this::toDTO);
  }

  @Override
  public List<DepartmentDTO> getAllDepartments() {
    return departmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
    Department department =
        departmentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND, CommonResultCode.PARAM_ERROR, "部门不存在", null));

    department.update(
        dto.deptName(),
        dto.deptCode(),
        dto.abbreviation(),
        dto.email(),
        dto.phone(),
        dto.leaderId(),
        dto.sortNum());

    return toDTO(departmentRepository.save(department));
  }

  @Override
  @Transactional
  public void deleteDepartment(Long id) {
    departmentRepository.deleteById(id);
  }

  private DepartmentDTO toDTO(Department entity) {
    return new DepartmentDTO(
        entity.getId(),
        entity.getParentId(),
        entity.getDeptName(),
        entity.getDeptCode(),
        entity.getAbbreviation(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getLeaderId(),
        entity.getSortNum(),
        entity.getStatus());
  }
}
