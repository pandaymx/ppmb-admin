package top.ppmblszdp.auth.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.auth.application.AuthService;
import top.ppmblszdp.auth.interfaces.web.dto.LoginCommand;
import top.ppmblszdp.auth.interfaces.web.dto.TokenDto;
import top.ppmblszdp.common.api.Result;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "登录接口")
  @PostMapping("/login")
  public Result<TokenDto> login(@RequestBody @Valid LoginCommand command) {
    TokenDto tokenDto = authService.login(command);
    return Result.success(tokenDto);
  }

  @Operation(summary = "注册接口")
  @PostMapping("/register")
  public Result<Void> register(
      @RequestBody @Valid top.ppmblszdp.auth.interfaces.web.dto.UserRegisterCommand command) {
    authService.register(command);
    return Result.success();
  }
}
