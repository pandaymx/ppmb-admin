package top.ppmblszdp.system.interfaces.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserApplicationService userApplicationService;

  @PostMapping
  public Result<UserDto> createUser(@Valid @RequestBody CreateUserCommand command) {
    return Result.success(userApplicationService.createUser(command));
  }

  @GetMapping("/{id}")
  public Result<UserDto> getUserById(@PathVariable Long id) {
    return userApplicationService.getUserById(id).map(Result::success).orElseGet(Result::success);
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteUser(@PathVariable Long id) {
    userApplicationService.deleteUser(id);
    return Result.success();
  }
}
