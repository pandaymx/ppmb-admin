package top.ppmblszdp.system.interfaces.web.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.role.RoleApplicationService;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

  @Mock private RoleApplicationService roleApplicationService;

  @InjectMocks private RoleController controller;

  @Test
  void testCreateRole() {
    CreateRoleCommand command = new CreateRoleCommand("Admin", "ROLE_ADMIN", "Desc", null);
    RoleDto dto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 1, false, LocalDateTime.now());
    when(roleApplicationService.createRole(command)).thenReturn(dto);
    Result<RoleDto> result = controller.createRole(command);
    assertEquals("00000", result.code());
    assertEquals(dto, result.data());
  }

  @Test
  void testUpdateRole() {
    UpdateRoleCommand command = new UpdateRoleCommand("Super Admin", "Super Desc", null);
    RoleDto dto =
        new RoleDto(1L, "Super Admin", "ROLE_ADMIN", "Super Desc", 1, false, LocalDateTime.now());
    when(roleApplicationService.updateRole(1L, command)).thenReturn(dto);
    Result<RoleDto> result = controller.updateRole(1L, command);
    assertEquals("00000", result.code());
    assertEquals(dto, result.data());
  }

  @Test
  void testDeleteRole() {
    Result<Void> result = controller.deleteRole(1L);
    verify(roleApplicationService, times(1)).deleteRole(1L);
    assertEquals("00000", result.code());
  }

  @Test
  void testGetRolePage() {
    RoleDto dto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 1, false, LocalDateTime.now());
    PageResult<RoleDto> page = PageResult.of(1, List.of(dto), 1, 10);
    when(roleApplicationService.getRolePage(any(), any())).thenReturn(page);
    Result<PageResult<RoleDto>> result =
        controller.getRolePage(new RolePageQuery(null, null), new PageQuery(1, 10));
    assertEquals("00000", result.code());
    assertEquals(page, result.data());
  }

  @Test
  void testGetRoleOptions() {
    RoleDto dto = new RoleDto(1L, "Admin", "ROLE_ADMIN", "Desc", 1, false, LocalDateTime.now());
    when(roleApplicationService.getRoleOptions()).thenReturn(List.of(dto));
    Result<List<RoleDto>> result = controller.getRoleOptions();
    assertEquals("00000", result.code());
    assertEquals(1, result.data().size());
  }
}
