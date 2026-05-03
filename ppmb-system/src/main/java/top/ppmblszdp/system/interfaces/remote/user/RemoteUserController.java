package top.ppmblszdp.system.interfaces.remote.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.feign.RemoteUserService;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

@RestController
@RequestMapping("/api/system/remote/user")
@RequiredArgsConstructor
public class RemoteUserController implements RemoteUserService {

  private final UserRepository userRepository;

  @Override
  @GetMapping("/info/{username}")
  public Result<SysUserDto> getUserInfo(@PathVariable("username") String username) {
    return userRepository
        .findByUsername(username)
        .map(
            user -> {
              SysUserDto dto = new SysUserDto();
              dto.setId(user.getId());
              dto.setUsername(user.getUsername());
              dto.setPassword(user.getPassword());
              dto.setNickname(user.getNickname());
              dto.setEmail(user.getEmail());
              dto.setPhone(user.getPhone());
              dto.setStatus(user.getStatus());
              return Result.success(dto);
            })
        .orElseGet(() -> Result.failure("USER_NOT_FOUND", "User not found"));
  }
}
