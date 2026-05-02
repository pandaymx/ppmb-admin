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
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDto;

@Service
@RequiredArgsConstructor
public class DepartmentApplicationServiceImpl implements DepartmentApplicationService {

  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional
  public DepartmentDto createDepartment(DepartmentDto dto) {
    Department department =
        Department.create(dto.deptName(), dto.deptCode(), dto.parentId(), dto.sortNum());
    return toDto(departmentRepository.save(department));
  }

  @Override
  public Optional<DepartmentDto> getDepartmentById(Long id) {
    return departmentRepository.findById(id).map(this::toDto);
  }

  @Override
  public List<DepartmentDto> getAllDepartments() {
    return departmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public DepartmentDto updateDepartment(Long id, DepartmentDto dto) {
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

    return toDto(departmentRepository.save(department));
  }

  @Override
  @Transactional
  public void deleteDepartment(Long id) {
    departmentRepository.deleteById(id);
  }

  private DepartmentDto toDto(Department entity) {
    return new DepartmentDto(
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
