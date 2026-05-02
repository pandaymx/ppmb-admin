package top.ppmblszdp.system.application.service.role.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.util.AssertUtils;
import top.ppmblszdp.system.application.service.role.RoleApplicationService;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

@Service
@RequiredArgsConstructor
public class RoleApplicationServiceImpl implements RoleApplicationService {

  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final top.ppmblszdp.system.application.assembler.RoleAssembler roleAssembler;

  @Override
  @Transactional
  public RoleDto createRole(CreateRoleCommand command) {
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    return roleAssembler.toDto(roleRepository.save(role));
  }

  @Override
  @Transactional
  public RoleDto updateRole(Long id, UpdateRoleCommand command) {
    Role role = roleRepository.findById(id).orElseThrow();
    AssertUtils.isTrue(
        !role.getIsReadonly(), CommonResultCode.PARAM_ERROR); // Built-in roles are read-only
    role.updateInfo(command.roleName(), command.description());
    return roleAssembler.toDto(roleRepository.save(role));
  }

  @Override
  @Transactional
  public void deleteRole(Long id) {
    Role role = roleRepository.findById(id).orElseThrow();
    AssertUtils.isTrue(
        !role.getIsReadonly(), CommonResultCode.PARAM_ERROR); // Cannot delete built-in roles

    long userCount = userRoleRepository.countByRoleId(id);
    AssertUtils.isTrue(
        userCount == 0, CommonResultCode.PARAM_ERROR); // Check if role has assigned users

    roleRepository.deleteById(id);
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
}
