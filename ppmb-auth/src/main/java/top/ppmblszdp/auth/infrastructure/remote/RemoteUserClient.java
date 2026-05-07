package top.ppmblszdp.auth.infrastructure.remote;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ppmblszdp.api.system.dto.SysUserDto;
import top.ppmblszdp.api.system.dto.UserRegisterDto;
import top.ppmblszdp.api.system.feign.RemoteUserService;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.Result;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteUserClient {

  /**
   * Resilience4j 实例名（同时作为 CircuitBreaker / RateLimiter 的实例名）.
   *
   * <p>命名建议与 Feign 的 contextId 保持一致，便于按“下游服务/接口”维度统一治理。
   */
  public static final String INSTANCE_NAME = "remoteUserService";

  private final RemoteUserService remoteUserService;

  @CircuitBreaker(name = INSTANCE_NAME, fallbackMethod = "getUserInfoFallback")
  @RateLimiter(name = INSTANCE_NAME, fallbackMethod = "getUserInfoFallback")
  public Result<SysUserDto> getUserInfo(String username) {
    return remoteUserService.getUserInfo(username);
  }

  @SuppressWarnings("unused")
  private Result<SysUserDto> getUserInfoFallback(String username, Throwable ex) {
    log.warn("remoteUserService.getUserInfo degraded for username={}, reason={}", username, ex.toString());
    return Result.failure(CommonResultCode.REMOTE_ERROR.getCode(), "用户服务暂不可用，请稍后重试");
  }

  @CircuitBreaker(name = INSTANCE_NAME, fallbackMethod = "registerUserFallback")
  @RateLimiter(name = INSTANCE_NAME, fallbackMethod = "registerUserFallback")
  public Result<SysUserDto> registerUser(UserRegisterDto dto) {
    return remoteUserService.registerUser(dto);
  }

  @SuppressWarnings("unused")
  private Result<SysUserDto> registerUserFallback(UserRegisterDto dto, Throwable ex) {
    log.warn("remoteUserService.registerUser degraded for username={}, reason={}", dto.username(), ex.toString());
    return Result.failure(CommonResultCode.REMOTE_ERROR.getCode(), "用户服务繁忙，请稍后重试");
  }
}

