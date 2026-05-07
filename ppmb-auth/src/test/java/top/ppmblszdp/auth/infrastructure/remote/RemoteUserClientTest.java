package top.ppmblszdp.auth.infrastructure.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.dto.UserRegisterDto;
import top.ppmblszdp.api.system.feign.RemoteUserService;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.Result;

@ExtendWith(MockitoExtension.class)
@DisplayName("远程用户客户端测试")
class RemoteUserClientTest {

  @Mock private RemoteUserService remoteUserService;

  @InjectMocks private RemoteUserClient remoteUserClient;

  @Test
  @DisplayName("获取用户信息时应调用 Feign 接口")
  void shouldCallRemoteUserServiceWhenGetUserInfo() {
    SysUserDto userDto =
        new SysUserDto(1L, "admin", "admin", "nickname", "admin@ppmb.com", "138", 1);
    when(remoteUserService.getUserInfo("admin")).thenReturn(Result.success(userDto));

    Result<SysUserDto> result = remoteUserClient.getUserInfo("admin");

    assertThat(result.code()).isEqualTo(CommonResultCode.SUCCESS.getCode());
    assertThat(result.data().username()).isEqualTo("admin");
    verify(remoteUserService).getUserInfo("admin");
  }

  @Test
  @DisplayName("注册用户时应调用 Feign 接口")
  void shouldCallRemoteUserServiceWhenRegisterUser() {
    UserRegisterDto registerDto = new UserRegisterDto("newuser", "password", "email", "nick");
    SysUserDto userDto = new SysUserDto(2L, "newuser", "pass", "nick", "email", null, 1);
    when(remoteUserService.registerUser(registerDto)).thenReturn(Result.success(userDto));

    Result<SysUserDto> result = remoteUserClient.registerUser(registerDto);

    assertThat(result.code()).isEqualTo(CommonResultCode.SUCCESS.getCode());
    assertThat(result.data().username()).isEqualTo("newuser");
    verify(remoteUserService).registerUser(registerDto);
  }

  @Test
  @DisplayName("获取用户信息异常时应触发降级逻辑")
  void shouldFallbackWhenGetUserInfoFails() {
    Object result =
        ReflectionTestUtils.invokeMethod(
            remoteUserClient, "getUserInfoFallback", "admin", new RuntimeException("service down"));

    @SuppressWarnings("unchecked")
    Result<SysUserDto> fallbackResult = (Result<SysUserDto>) result;
    assertThat(fallbackResult.code()).isEqualTo(CommonResultCode.REMOTE_ERROR.getCode());
  }

  @Test
  @DisplayName("注册用户异常时应触发降级逻辑")
  void shouldFallbackWhenRegisterUserFails() {
    UserRegisterDto dto = new UserRegisterDto("admin", "123", "a@b.com", "nick");
    Object result =
        ReflectionTestUtils.invokeMethod(
            remoteUserClient, "registerUserFallback", dto, new RuntimeException("service down"));

    @SuppressWarnings("unchecked")
    Result<SysUserDto> fallbackResult = (Result<SysUserDto>) result;
    assertThat(fallbackResult.code()).isEqualTo(CommonResultCode.REMOTE_ERROR.getCode());
  }
}
