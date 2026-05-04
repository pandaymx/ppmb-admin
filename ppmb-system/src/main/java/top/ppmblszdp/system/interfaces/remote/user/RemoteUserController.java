package top.ppmblszdp.system.interfaces.remote.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final top.ppmblszdp.system.application.service.user.UserApplicationService
      userApplicationService;

  @Override
  @GetMapping("/info/{username}")
  public Result<SysUserDto> getUserInfo(@PathVariable("username") String username) {
    return userRepository
        .findByUsername(username)
        .map(
            user -> {
              SysUserDto dto =
                  new SysUserDto(
                      user.getId(),
                      user.getUsername(),
                      user.getPassword(),
                      user.getNickname(),
                      user.getEmail(),
                      user.getPhone(),
                      user.getStatus());
              return Result.success(dto);
            })
        .orElseGet(() -> Result.failure("USER_NOT_FOUND", "User not found"));
  }

  @Override
  @PostMapping("/register")
  public Result<SysUserDto> registerUser(
      @RequestBody top.ppmblszdp.api.system.dto.UserRegisterDto userRegisterDto) {
    top.ppmblszdp.system.interfaces.web.user.dto.UserDto userDto =
        userApplicationService.registerUser(userRegisterDto);
    return Result.success(
        new SysUserDto(
            userDto.id(),
            userDto.username(),
            null, // Don't return password
            userDto.nickname(),
            userDto.email(),
            userDto.phone(),
            userDto.status()));
  }
}
