package top.ppmblszdp.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.common.api.feign.SysUserFeignClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserContextService 单元测试")
class UserContextServiceTest {

  @Mock private SysUserFeignClient sysUserFeignClient;

  @InjectMocks private UserContextService userContextService;

  @Test
  @DisplayName("应能通过 Feign 客户端获取用户信息")
  void shouldGetUserInfoViaFeign() {
    // Arrange
    Long userId = 1L;
    String mockUser = "{ \"id\": 1, \"username\": \"admin\" }";
    when(sysUserFeignClient.getUserById(userId)).thenReturn(mockUser);

    // Act
    Object result = userContextService.getUserInfo(userId);

    // Assert
    assertEquals(mockUser, result);
  }
}
