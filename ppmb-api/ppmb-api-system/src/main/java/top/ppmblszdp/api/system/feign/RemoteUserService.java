package top.ppmblszdp.api.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.dto.UserRegisterDto;
import top.ppmblszdp.common.api.Result;

@FeignClient(
    contextId = "remoteUserService",
    value = "ppmb-system",
    path = "/api/system/remote/user")
public interface RemoteUserService {

  @GetMapping("/info/{username}")
  Result<SysUserDto> getUserInfo(@PathVariable("username") String username);

  @PostMapping(value = "/register", consumes = "application/json")
  Result<SysUserDto> registerUser(@RequestBody UserRegisterDto userRegisterDto);
}
