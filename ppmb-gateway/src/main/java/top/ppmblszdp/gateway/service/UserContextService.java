package top.ppmblszdp.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.ppmblszdp.common.api.feign.SysUserFeignClient;

/** 用户上下文服务，演示 Feign 客户端的使用. */
@Service
@RequiredArgsConstructor
public class UserContextService {

  private final SysUserFeignClient sysUserFeignClient;

  /**
   * 获取当前用户信息（示例）.
   *
   * @param userId 用户 ID
   * @return 用户信息对象
   */
  public Object getUserInfo(Long userId) {
    return sysUserFeignClient.getUserById(userId);
  }
}
