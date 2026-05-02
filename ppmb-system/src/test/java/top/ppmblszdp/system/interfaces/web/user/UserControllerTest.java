package top.ppmblszdp.system.interfaces.web.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.role.UserRoleApplicationService;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户管理接口测试")
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserApplicationService userApplicationService;
  @Mock private UserRoleApplicationService userRoleApplicationService;

  @InjectMocks private UserController userController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  @DisplayName("创建用户")
  void createUser() throws Exception {
    CreateUserCommand command =
        new CreateUserCommand("testuser", "password123", "Tester", "test@example.com", "123456");
    UserDto userDto = new UserDto(1L, "testuser", "Tester", "test@example.com", "123456", 0);

    when(userApplicationService.createUser(any(CreateUserCommand.class))).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.username").value("testuser"))
        .andExpect(jsonPath("$.data.nickname").value("Tester"));
  }

  @Test
  void testGetUserRoles() {
    when(userRoleApplicationService.getUserRoles(1L)).thenReturn(List.of(2L, 3L));
    Result<List<Long>> result = userController.getUserRoles(1L);
    assertEquals("00000", result.code());
    assertEquals(2, result.data().size());
  }

  @Test
  void testAssignRoles() {
    Result<Void> result = userController.assignRoles(1L, List.of(2L, 3L));
    verify(userRoleApplicationService, times(1)).assignRolesToUser(1L, List.of(2L, 3L));
    assertEquals("00000", result.code());
  }

  @Test
  void testBatchAssignRoles() {
    BatchUserRoleCommand command = new BatchUserRoleCommand(List.of(1L, 2L), List.of(3L, 4L));
    Result<Void> result = userController.batchAssignRoles(command);
    verify(userRoleApplicationService, times(1)).batchAssignRoles(command);
    assertEquals("00000", result.code());
  }
}
