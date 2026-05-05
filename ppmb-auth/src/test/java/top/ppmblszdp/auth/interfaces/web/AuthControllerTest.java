package top.ppmblszdp.auth.interfaces.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.auth.application.AuthService;
import top.ppmblszdp.auth.interfaces.web.dto.LoginCommand;
import top.ppmblszdp.auth.interfaces.web.dto.TokenDto;
import top.ppmblszdp.common.api.Result;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  @Test
  @DisplayName("登录成功")
  void loginSuccess() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    TokenDto tokenDto = new TokenDto("test-token", 7200L);

    when(authService.login(command)).thenReturn(tokenDto);

    // Act
    Result<TokenDto> result = authController.login(command);

    // Assert
    assertNotNull(result);
    assertEquals("00000", result.code());
    assertEquals("test-token", result.data().accessToken());
    assertEquals(7200L, result.data().expiresIn());
  }

  @Test
  @DisplayName("登录调用服务层")
  void loginCallsService() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    TokenDto tokenDto = new TokenDto("test-token", 7200L);

    when(authService.login(command)).thenReturn(tokenDto);

    // Act
    authController.login(command);

    // Assert
    verify(authService, times(1)).login(command);
  }

  @Test
  @DisplayName("注册成功")
  void registerSuccess() {
    // Arrange
    final var command =
        new top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand(
            "testuser", "password123", "test@example.com", "nickname");

    doNothing().when(authService).register(command);

    // Act
    Result<Void> result = authController.register(command);

    // Assert
    assertNotNull(result);
    assertEquals("00000", result.code());
  }

  @Test
  @DisplayName("注册调用服务层")
  void registerCallsService() {
    // Arrange
    final var command =
        new top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand(
            "testuser", "password123", "test@example.com", "nickname");

    doNothing().when(authService).register(command);

    // Act
    authController.register(command);

    // Assert
    verify(authService, times(1)).register(command);
  }
}
