package top.ppmblszdp.auth.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.feign.RemoteUserService;
import top.ppmblszdp.auth.dto.LoginCommand;
import top.ppmblszdp.auth.dto.TokenDto;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final RemoteUserService remoteUserService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final PpmbSecurityProperties securityProperties;

  public TokenDto login(LoginCommand command) {
    Result<SysUserDto> result = remoteUserService.getUserInfo(command.getUsername());

    if (!CommonResultCode.SUCCESS.getCode().equals(result.code()) || result.data() == null) {
      log.warn(
          "Login failed: User not found or error calling system module for {}",
          command.getUsername());
      throw new top.ppmblszdp.common.exception.BusinessException("Invalid username or password");
    }

    SysUserDto user = result.data();

    if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
      log.warn("Login failed: Invalid password for {}", command.getUsername());
      throw new top.ppmblszdp.common.exception.BusinessException("Invalid username or password");
    }

    if (user.getStatus() != null && user.getStatus() != 0) {
      log.warn("Login failed: User is disabled {}", command.getUsername());
      throw new top.ppmblszdp.common.exception.BusinessException("User account is disabled");
    }

    Map<String, Object> claims =
        Map.of(
            "userId", user.getId(),
            "username", user.getUsername());

    String token = jwtUtils.createToken(user.getUsername(), claims);
    return new TokenDto(token, securityProperties.getJwt().getExpire());
  }
}
