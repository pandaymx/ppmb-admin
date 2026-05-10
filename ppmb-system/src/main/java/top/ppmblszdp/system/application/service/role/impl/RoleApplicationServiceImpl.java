package top.ppmblszdp.system.application.service.role.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.util.AssertUtils;
import top.ppmblszdp.system.application.assembler.RoleAssembler;
import top.ppmblszdp.system.application.service.role.RoleApplicationService;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.entity.RoleDept;
import top.ppmblszdp.system.domain.model.role.repository.RoleDeptRepository;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleDataScopeCommand;

@Service
@RequiredArgsConstructor
public class RoleApplicationServiceImpl implements RoleApplicationService {

  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleDeptRepository roleDeptRepository;
  private final RoleAssembler roleAssembler;
  private final RoleMenuApplicationService roleMenuApplicationService;

  @Override
  @Transactional
  public RoleDto createRole(CreateRoleCommand command) {
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    Role savedRole = roleRepository.save(role);
    java.util.Optional.ofNullable(command.menuIds())
        .filter(ids -> !ids.isEmpty())
        .ifPresent(ids -> roleMenuApplicationService.assignMenusToRole(savedRole.getId(), ids));
    return roleAssembler.toDto(savedRole);
  }

  @Override
  @Transactional
  public RoleDto updateRole(Long id, UpdateRoleCommand command) {
    Role role = roleRepository.findById(id).orElseThrow();
    AssertUtils.isTrue(!role.getIsReadonly(), CommonResultCode.PARAM_ERROR);
    role.updateInfo(command.roleName(), command.description());
    Role savedRole = roleRepository.save(role);
    java.util.Optional.ofNullable(command.menuIds())
        .ifPresent(ids -> roleMenuApplicationService.assignMenusToRole(savedRole.getId(), ids));
    return roleAssembler.toDto(savedRole);
  }

  @Override
  @Transactional
  public void deleteRole(Long id) {
    Role role = roleRepository.findById(id).orElseThrow();
    AssertUtils.isTrue(!role.getIsReadonly(), CommonResultCode.PARAM_ERROR);

    long userCount = userRoleRepository.countByRoleId(id);
    AssertUtils.isTrue(userCount == 0, CommonResultCode.PARAM_ERROR);

    roleRepository.deleteById(id);
    roleDeptRepository.deleteByRoleId(id);
  }

  @Override
  public PageResult<RoleDto> getRolePage(RolePageQuery query, PageQuery pageQuery) {
    PageResult<Role> page = roleRepository.findPage(query.name(), query.status(), pageQuery);
    List<RoleDto> dtos = roleAssembler.toDtoList(page.list());
    return PageResult.of(page.total(), dtos, page.pageNum(), page.pageSize());
  }

  @Override
  public List<RoleDto> getRoleOptions() {
    return roleAssembler.toDtoList(roleRepository.findAll());
  }

  @Override
  @Transactional
  public void updateRoleDataScope(Long id, UpdateRoleDataScopeCommand command) {
    Role role = roleRepository.findById(id).orElseThrow();
    AssertUtils.isTrue(!role.getIsReadonly(), CommonResultCode.PARAM_ERROR);

    role.setDataScopeValue(command.dataScope());
    roleRepository.save(role);

    roleDeptRepository.deleteByRoleId(id);

    // 如果是自定义权限，保存关联的部门
    if (command.dataScope() == 2 && command.deptIds() != null && !command.deptIds().isEmpty()) {
      List<RoleDept> roleDepts =
          command.deptIds().stream().map(deptId -> RoleDept.create(id, deptId)).toList();
      roleDeptRepository.saveAll(roleDepts);
    }
  }

  @Override
  public List<Long> getRoleDeptIds(Long id) {
    return roleDeptRepository.findDeptIdsByRoleId(id);
  }
}
