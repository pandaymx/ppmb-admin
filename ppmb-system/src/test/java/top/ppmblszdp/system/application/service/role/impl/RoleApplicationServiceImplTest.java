package top.ppmblszdp.system.application.service.role.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

@ExtendWith(MockitoExtension.class)
class RoleApplicationServiceImplTest {

  @Mock private RoleRepository roleRepository;
  @Mock private UserRoleRepository userRoleRepository;
  @Mock private top.ppmblszdp.system.application.assembler.RoleAssembler roleAssembler;

  @InjectMocks private RoleApplicationServiceImpl service;

  @Test
  void testCreateRole() {
    CreateRoleCommand command = new CreateRoleCommand("Admin", "ROLE_ADMIN", "Desc", null);
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, null);
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);
    RoleDto dto = service.createRole(command);
    assertEquals("Admin", dto.roleName());
  }

  @Test
  void testUpdateRole() {
    UpdateRoleCommand command = new UpdateRoleCommand("Super Admin", "Super Desc", null);
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto = new RoleDto(1L, "Super Admin", "ROLE_ADMIN", "Super Desc", 0, false, null);
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);
    RoleDto dto = service.updateRole(1L, command);
    assertEquals("Super Admin", dto.roleName());
  }

  @Test
  @DisplayName("更新角色失败-只读角色")
  void testUpdateRole_readonly() {
    UpdateRoleCommand command = new UpdateRoleCommand("New Name", "New Desc", null);
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    org.springframework.test.util.ReflectionTestUtils.setField(role, "isReadonly", true);
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    assertThrows(BusinessException.class, () -> service.updateRole(1L, command));
  }

  @Test
  @DisplayName("删除角色失败-只读角色")
  void testDeleteRole_readonly() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    org.springframework.test.util.ReflectionTestUtils.setField(role, "isReadonly", true);
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    assertThrows(BusinessException.class, () -> service.deleteRole(1L));
  }

  @Test
  @DisplayName("删除角色成功")
  void testDeleteRole() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(userRoleRepository.countByRoleId(1L)).thenReturn(0L);
    service.deleteRole(1L);
    verify(roleRepository, times(1)).deleteById(1L);
  }

  @Test
  @DisplayName("删除角色失败-有关联用户")
  void testDeleteRoleWithUsersAssigned() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(userRoleRepository.countByRoleId(1L)).thenReturn(1L);
    assertThrows(BusinessException.class, () -> service.deleteRole(1L));
  }

  @Test
  void testGetRolePage() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    PageResult<Role> page = PageResult.of(1, List.of(role), 1, 10);
    when(roleRepository.findPage(any(), any(), any())).thenReturn(page);
    RoleDto dummyDto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, null);
    when(roleAssembler.toDtoList(any())).thenReturn(List.of(dummyDto));
    PageResult<RoleDto> result =
        service.getRolePage(new RolePageQuery(null, null), new PageQuery(1, 10));
    assertEquals(1, result.total());
  }

  @Test
  void testGetRoleOptions() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(roleRepository.findAll()).thenReturn(List.of(role));
    RoleDto dummyDto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, null);
    when(roleAssembler.toDtoList(any())).thenReturn(List.of(dummyDto));
    List<RoleDto> list = service.getRoleOptions();
    assertEquals(1, list.size());
  }
}
