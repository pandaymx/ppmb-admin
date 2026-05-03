package top.ppmblszdp.common.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** 系统用户 Feign 客户端示例. */
@FeignClient(name = "ppmb-system", contextId = "sysUser")
public interface SysUserFeignClient {

  /**
   * 根据用户 ID 获取用户详细信息（示例）.
   *
   * @param userId 用户 ID
   * @return 用户信息（此处应为具体的 DTO）
   */
  @GetMapping("/sys/user/{userId}")
  Object getUserById(@PathVariable("userId") Long userId);
}
