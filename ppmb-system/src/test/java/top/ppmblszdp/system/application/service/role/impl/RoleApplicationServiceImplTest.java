package top.ppmblszdp.system.application.service.role.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.assembler.RoleAssembler;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("角色应用服务实现单元测试")
class RoleApplicationServiceImplTest {

  @Mock private RoleRepository roleRepository;
  @Mock private UserRoleRepository userRoleRepository;
  @Mock private top.ppmblszdp.system.domain.model.role.repository.RoleDeptRepository roleDeptRepository;
  @Mock private RoleAssembler roleAssembler;
  @Mock private RoleMenuApplicationService roleMenuApplicationService;

  @InjectMocks private RoleApplicationServiceImpl service;

  @Test
  @DisplayName("创建角色-成功（不带菜单）")
  void testCreateRole() {
    CreateRoleCommand command = new CreateRoleCommand("Admin", "ROLE_ADMIN", "Desc", null);
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto =
        new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);

    RoleDto dto = service.createRole(command);

    assertEquals("Admin", dto.roleName());
    verify(roleMenuApplicationService, never()).assignMenusToRole(any(), any());
  }

  @Test
  @DisplayName("创建角色-成功（带菜单）")
  void testCreateRoleWithMenus() {
    List<Long> menuIds = List.of(1L, 2L);
    CreateRoleCommand command = new CreateRoleCommand("Admin", "ROLE_ADMIN", "Desc", menuIds);
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    ReflectionTestUtils.setField(role, "id", 100L);

    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto =
        new RoleDto(100L, "Admin", "ROLE_ADMIN", "Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);

    RoleDto dto = service.createRole(command);

    assertEquals(100L, dto.id());
    verify(roleMenuApplicationService).assignMenusToRole(100L, menuIds);
  }

  @Test
  @DisplayName("创建角色-成功（菜单列表为空）")
  void testCreateRoleWithEmptyMenus() {
    List<Long> menuIds = List.of();
    CreateRoleCommand command = new CreateRoleCommand("Admin", "ROLE_ADMIN", "Desc", menuIds);
    Role role = Role.create(command.roleName(), command.roleCode(), command.description());
    ReflectionTestUtils.setField(role, "id", 100L);

    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto =
        new RoleDto(100L, "Admin", "ROLE_ADMIN", "Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);

    service.createRole(command);

    verify(roleMenuApplicationService, never()).assignMenusToRole(any(), any());
  }

  @Test
  @DisplayName("更新角色-成功")
  void testUpdateRole() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    ReflectionTestUtils.setField(role, "id", 100L);

    UpdateRoleCommand command = new UpdateRoleCommand("Super Admin", "Super Desc", List.of(3L));
    when(roleRepository.findById(100L)).thenReturn(Optional.of(role));
    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto =
        new RoleDto(100L, "Super Admin", "ROLE_ADMIN", "Super Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);

    RoleDto dto = service.updateRole(100L, command);

    assertEquals("Super Admin", dto.roleName());
    verify(roleMenuApplicationService).assignMenusToRole(100L, List.of(3L));
  }

  @Test
  @DisplayName("更新角色-成功（菜单列表为 null）")
  void testUpdateRoleWithNullMenus() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    ReflectionTestUtils.setField(role, "id", 100L);

    when(roleRepository.findById(100L)).thenReturn(Optional.of(role));
    when(roleRepository.save(any(Role.class))).thenReturn(role);
    RoleDto dummyDto =
        new RoleDto(100L, "Super Admin", "ROLE_ADMIN", "Super Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDto(any(Role.class))).thenReturn(dummyDto);

    UpdateRoleCommand command = new UpdateRoleCommand("Super Admin", "Super Desc", null);
    service.updateRole(100L, command);

    verify(roleMenuApplicationService, never()).assignMenusToRole(any(), any());
  }

  @Test
  @DisplayName("更新角色失败-只读角色")
  void testUpdateRole_readonly() {
    UpdateRoleCommand command = new UpdateRoleCommand("New Name", "New Desc", null);
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    ReflectionTestUtils.setField(role, "isReadonly", true);
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    assertThrows(BusinessException.class, () -> service.updateRole(1L, command));
  }

  @Test
  @DisplayName("删除角色失败-只读角色")
  void testDeleteRole_readonly() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    ReflectionTestUtils.setField(role, "isReadonly", true);
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
    verify(roleDeptRepository, times(1)).deleteByRoleId(1L);
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
  @DisplayName("分页查询角色")
  void testGetRolePage() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    PageResult<Role> page = PageResult.of(1, List.of(role), 1, 10);
    when(roleRepository.findPage(any(), any(), any())).thenReturn(page);
    RoleDto dummyDto =
        new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDtoList(any())).thenReturn(List.of(dummyDto));

    PageResult<RoleDto> result =
        service.getRolePage(new RolePageQuery(null, null), new PageQuery(1, 10));

    assertEquals(1, result.total());
  }

  @Test
  @DisplayName("获取角色选项")
  void testGetRoleOptions() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(roleRepository.findAll()).thenReturn(List.of(role));
    RoleDto dummyDto =
        new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 0, false, LocalDateTime.now());
    when(roleAssembler.toDtoList(any())).thenReturn(List.of(dummyDto));

    List<RoleDto> list = service.getRoleOptions();

    assertEquals(1, list.size());
  }
}
