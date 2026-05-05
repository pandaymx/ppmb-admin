package top.ppmblszdp.system.interfaces.remote.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import top.ppmblszdp.api.system.dto.UserRegisterDto;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("远程用户控制器单元测试")
class RemoteUserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserRepository userRepository;
  @Mock private UserApplicationService userApplicationService;

  @InjectMocks private RemoteUserController remoteUserController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(remoteUserController).build();
  }

  @Test
  @DisplayName("获取用户信息成功")
  void getUserInfo_Success() throws Exception {
    String username = "admin";
    User user = User.create(username, "password", "Admin");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    mockMvc
        .perform(get("/api/system/remote/user/info/" + username))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("00000"))
        .andExpect(jsonPath("$.data.username").value(username));
  }

  @Test
  @DisplayName("用户信息不存在应返回失败")
  void getUserInfo_NotFound() throws Exception {
    String username = "nonexistent";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/system/remote/user/info/" + username))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  @DisplayName("注册用户成功")
  void registerUser_Success() throws Exception {
    UserDto userDto = new UserDto(1L, "newuser", "Nickname", "email@test.com", null, 1);

    when(userApplicationService.registerUser(any(UserRegisterDto.class))).thenReturn(userDto);

    mockMvc
        .perform(
            post("/api/system/remote/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\":\"newuser\",\"password\":\"password\",\"nickname\":\"Nickname\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("00000"))
        .andExpect(jsonPath("$.data.username").value("newuser"))
        .andExpect(jsonPath("$.data.password").isEmpty());
  }
}
