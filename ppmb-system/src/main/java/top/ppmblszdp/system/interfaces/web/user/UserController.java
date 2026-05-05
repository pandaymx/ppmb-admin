package top.ppmblszdp.system.interfaces.web.user;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.security.util.SecurityUtils;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.application.service.role.UserRoleApplicationService;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserApplicationService userApplicationService;
  private final UserRoleApplicationService userRoleApplicationService;
  private final MenuApplicationService menuApplicationService;

  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.CREATED)
  public UserDto createUser(@Valid @RequestBody CreateUserCommand command) {
    return userApplicationService.createUser(command);
  }

  @GetMapping("/{id}")
  public UserDto getUserById(@PathVariable Long id) {
    return userApplicationService
        .getUserById(id)
        .orElseThrow(
            () ->
                new top.ppmblszdp.common.exception.BusinessException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    top.ppmblszdp.common.api.CommonResultCode.USER_ERROR,
                    "用户不存在",
                    null));
  }

  @DeleteMapping("/{id}")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userApplicationService.deleteUser(id);
  }

  @GetMapping("/{id}/roles")
  public List<Long> getUserRoles(@PathVariable Long id) {
    return userRoleApplicationService.getUserRoles(id);
  }

  @PutMapping("/{id}/roles")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
    userRoleApplicationService.assignRolesToUser(id, roleIds);
  }

  @PostMapping("/batch/roles")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void batchAssignRoles(@Valid @RequestBody BatchUserRoleCommand command) {
    userRoleApplicationService.batchAssignRoles(command);
  }

  @GetMapping("/permissions")
  public List<String> getPermissions() {
    Long userId = SecurityUtils.getUserId();
    return menuApplicationService.getMenuPermsByUserId(userId);
  }
}
