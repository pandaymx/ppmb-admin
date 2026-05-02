package top.ppmblszdp.system.application.service.user.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户应用服务测试")
class UserApplicationServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserApplicationServiceImpl userService;

  private User user;
  private CreateUserCommand command;

  @BeforeEach
  void setUp() {
    user = User.create("testuser", "password123", "Tester");
    command =
        new CreateUserCommand("testuser", "password123", "Tester", "test@example.com", "123456");
  }

  @Test
  @DisplayName("创建用户成功")
  void createUser() {
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserDto result = userService.createUser(command);

    assertNotNull(result);
    assertEquals("testuser", result.username());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("根据 ID 获取用户成功")
  void getUserById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    Optional<UserDto> result = userService.getUserById(1L);

    assertTrue(result.isPresent());
    assertEquals("testuser", result.get().username());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("根据 ID 获取用户失败")
  void getUserByIdNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<UserDto> result = userService.getUserById(1L);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("删除用户成功")
  void deleteUser() {
    doNothing().when(userRepository).deleteById(1L);

    userService.deleteUser(1L);

    verify(userRepository, times(1)).deleteById(1L);
  }
}
