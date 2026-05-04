package top.ppmblszdp.auth.application;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.feign.RemoteUserService;
import top.ppmblszdp.auth.domain.exception.AccountDisabledException;
import top.ppmblszdp.auth.domain.exception.InvalidCredentialsException;
import top.ppmblszdp.auth.interfaces.web.dto.LoginCommand;
import top.ppmblszdp.auth.interfaces.web.dto.TokenDto;
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
    SysUserDto user =
        Optional.of(remoteUserService.getUserInfo(command.username()))
            .filter(res -> CommonResultCode.SUCCESS.getCode().equals(res.code()))
            .map(Result::data)
            .orElseThrow(
                () -> {
                  log.warn("Login failed: User not found or error for {}", command.username());
                  return new InvalidCredentialsException();
                });

    if (!passwordEncoder.matches(command.password(), user.password())) {
      log.warn("Login failed: Invalid password for {}", command.username());
      throw new InvalidCredentialsException();
    }

    Optional.ofNullable(user.status())
        .filter(status -> status != 0)
        .ifPresent(
            status -> {
              log.warn("Login failed: User is disabled {}", command.username());
              throw new AccountDisabledException();
            });

    Map<String, Object> claims =
        Map.of(
            "userId", user.id(),
            "username", user.username());

    String token = jwtUtils.createToken(user.username(), claims);
    return new TokenDto(token, securityProperties.getJwt().getExpire());
  }
}
