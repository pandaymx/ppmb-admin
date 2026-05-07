package top.ppmblszdp.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.auth.domain.exception.AccountDisabledException;
import top.ppmblszdp.auth.domain.exception.InvalidCredentialsException;
import top.ppmblszdp.auth.infrastructure.remote.RemoteUserClient;
import top.ppmblszdp.auth.interfaces.web.dto.LoginCommand;
import top.ppmblszdp.auth.interfaces.web.dto.TokenDto;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

  @Mock private RemoteUserClient remoteUserClient;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtUtils jwtUtils;
  @Mock private PpmbSecurityProperties securityProperties;

  @InjectMocks private AuthService authService;

  @Test
  @DisplayName("登录成功")
  void loginSuccess() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    SysUserDto user =
        new SysUserDto(
            1L, "testuser", "encryptedPassword", "Test User", "test@example.com", "13800000000", 0);
    Result<SysUserDto> result = Result.success(user);

    when(remoteUserClient.getUserInfo("testuser")).thenReturn(result);
    when(passwordEncoder.matches("password123", "encryptedPassword")).thenReturn(true);
    when(jwtUtils.createToken(eq("testuser"), anyMap())).thenReturn("test-token");

    PpmbSecurityProperties.Jwt jwtConfig = new PpmbSecurityProperties.Jwt();
    jwtConfig.setExpire(7200L);
    when(securityProperties.getJwt()).thenReturn(jwtConfig);

    // Act
    TokenDto tokenDto = authService.login(command);

    // Assert
    assertNotNull(tokenDto);
    assertEquals("test-token", tokenDto.accessToken());
    assertEquals(7200L, tokenDto.expiresIn());
  }

  @Test
  @DisplayName("登录失败 - 用户不存在")
  void loginFailedUserNotFound() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    Result<SysUserDto> result = Result.failure(CommonResultCode.USER_ERROR);

    when(remoteUserClient.getUserInfo("testuser")).thenReturn(result);

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> authService.login(command));
  }

  @Test
  @DisplayName("登录失败 - 密码错误")
  void loginFailedWrongPassword() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    SysUserDto user =
        new SysUserDto(
            1L, "testuser", "encryptedPassword", "Test User", "test@example.com", "13800000000", 0);
    Result<SysUserDto> result = Result.success(user);

    when(remoteUserClient.getUserInfo("testuser")).thenReturn(result);
    when(passwordEncoder.matches("password123", "encryptedPassword")).thenReturn(false);

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> authService.login(command));
  }

  @Test
  @DisplayName("登录失败 - 用户被禁用")
  void loginFailedUserDisabled() {
    // Arrange
    final LoginCommand command = new LoginCommand("testuser", "password123");
    SysUserDto user =
        new SysUserDto(
            1L, "testuser", "encryptedPassword", "Test User", "test@example.com", "13800000000", 1);
    Result<SysUserDto> result = Result.success(user);

    when(remoteUserClient.getUserInfo("testuser")).thenReturn(result);
    when(passwordEncoder.matches("password123", "encryptedPassword")).thenReturn(true);

    // Act & Assert
    assertThrows(AccountDisabledException.class, () -> authService.login(command));
  }

  @Test
  @DisplayName("注册成功")
  void registerSuccess() {
    // Arrange
    top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand command =
        new top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand(
            "testuser", "password123", "test@example.com", "Tester");
    SysUserDto userDto =
        new SysUserDto(1L, "testuser", null, "Tester", "test@example.com", null, 0);
    when(remoteUserClient.registerUser(any())).thenReturn(Result.success(userDto));

    // Act
    assertDoesNotThrow(() -> authService.register(command));

    // Assert
    verify(remoteUserClient, times(1)).registerUser(any());
  }

  @Test
  @DisplayName("注册失败")
  void registerFailed() {
    // Arrange
    top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand command =
        new top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand(
            "testuser", "password123", "test@example.com", "Tester");
    when(remoteUserClient.registerUser(any()))
        .thenReturn(Result.failure("ERROR", "User already exists"));

    // Act & Assert
    assertThrows(
        top.ppmblszdp.common.exception.BusinessException.class,
        () -> authService.register(command));
  }
}
