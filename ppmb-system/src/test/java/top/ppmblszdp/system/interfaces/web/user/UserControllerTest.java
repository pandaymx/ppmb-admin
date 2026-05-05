package top.ppmblszdp.system.interfaces.web.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.application.service.role.UserRoleApplicationService;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户控制器单元测试")
class UserControllerTest {

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock private UserApplicationService userApplicationService;

  @Mock private UserRoleApplicationService userRoleApplicationService;

  @Mock private MenuApplicationService menuApplicationService;

  @InjectMocks private UserController userController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new top.ppmblszdp.common.handler.GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("测试创建用户")
  void testCreateUser() throws Exception {
    CreateUserCommand command =
        new CreateUserCommand(
            "testuser", "password123", "Test User", "test@example.com", "13800000000");
    UserDto userDto =
        new UserDto(1L, "testuser", "Test User", "test@example.com", "13800000000", 1);

    when(userApplicationService.createUser(any(CreateUserCommand.class))).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @Test
  @DisplayName("测试通过 ID 获取用户")
  void testGetUserById() throws Exception {
    UserDto userDto =
        new UserDto(1L, "testuser", "Test User", "test@example.com", "13800000000", 1);

    when(userApplicationService.getUserById(1L)).thenReturn(Optional.of(userDto));

    mockMvc
        .perform(get("/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("测试删除用户")
  void testDeleteUser() throws Exception {
    doNothing().when(userApplicationService).deleteUser(1L);

    mockMvc.perform(delete("/users/1")).andExpect(status().isNoContent());

    verify(userApplicationService, times(1)).deleteUser(1L);
  }

  @Test
  @DisplayName("测试获取用户角色")
  void testGetUserRoles() throws Exception {
    when(userRoleApplicationService.getUserRoles(1L)).thenReturn(List.of(1L, 2L));

    mockMvc
        .perform(get("/users/1/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0]").value(1));
  }

  @Test
  @DisplayName("测试分配角色")
  void testAssignRoles() throws Exception {
    doNothing().when(userRoleApplicationService).assignRolesToUser(eq(1L), anyList());

    mockMvc
        .perform(
            put("/users/1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("测试获取用户权限")
  void testGetPermissions() throws Exception {
    try (var mockedSecurityUtils =
        mockStatic(top.ppmblszdp.common.security.util.SecurityUtils.class)) {
      mockedSecurityUtils
          .when(top.ppmblszdp.common.security.util.SecurityUtils::getUserId)
          .thenReturn(1L);
      when(menuApplicationService.getMenuPermsByUserId(1L)).thenReturn(List.of("sys:user:list"));

      mockMvc
          .perform(get("/users/permissions"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0]").value("sys:user:list"));
    }
  }

  @Test
  @DisplayName("测试更新用户角色")
  void testUpdateUserRoles() throws Exception {
    doNothing().when(userRoleApplicationService).assignRolesToUser(eq(1L), anyList());

    mockMvc
        .perform(
            put("/users/1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("测试批量分配角色")
  void testBatchAssignRoles() throws Exception {
    doNothing().when(userRoleApplicationService).batchAssignRoles(any());

    top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand command =
        new top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand(
            List.of(1L), List.of(100L));

    mockMvc
        .perform(
            post("/users/batch/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("测试用户未找到")
  void testGetUserByIdNotFound() throws Exception {
    when(userApplicationService.getUserById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/users/1")).andExpect(status().isNotFound());
  }
}
