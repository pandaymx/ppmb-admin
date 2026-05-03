package top.ppmblszdp.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.auth.dto.LoginCommand;
import top.ppmblszdp.auth.dto.TokenDto;
import top.ppmblszdp.auth.service.AuthService;
import top.ppmblszdp.common.api.Result;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public Result<TokenDto> login(@RequestBody @Valid LoginCommand command) {
    TokenDto tokenDto = authService.login(command);
    return Result.success(tokenDto);
  }
}
