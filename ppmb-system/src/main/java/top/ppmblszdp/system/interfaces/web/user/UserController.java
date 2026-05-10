package top.ppmblszdp.system.interfaces.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.security.util.SecurityUtils;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.application.service.role.UserRoleApplicationService;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;
import top.ppmblszdp.system.interfaces.web.user.dto.UserPageQuery;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserApplicationService userApplicationService;
  private final UserRoleApplicationService userRoleApplicationService;
  private final MenuApplicationService menuApplicationService;

  @Operation(summary = "分页获取用户列表")
  @GetMapping
  public Result<PageResult<UserDto>> pageUsers(UserPageQuery query, PageQuery pageQuery) {
    return Result.success(userApplicationService.pageUsers(query, pageQuery));
  }

  @Operation(summary = "创建用户")
  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.CREATED)
  public UserDto createUser(@Valid @RequestBody CreateUserCommand command) {
    return userApplicationService.createUser(command);
  }

  @Operation(summary = "根据ID获取用户")
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

  @Operation(summary = "删除用户")
  @DeleteMapping("/{id}")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userApplicationService.deleteUser(id);
  }

  @Operation(summary = "获取用户角色ID列表")
  @GetMapping("/{id}/roles")
  public List<Long> getUserRoles(@PathVariable Long id) {
    return userRoleApplicationService.getUserRoles(id);
  }

  @Operation(summary = "分配角色给用户")
  @PutMapping("/{id}/roles")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
    userRoleApplicationService.assignRolesToUser(id, roleIds);
  }

  @Operation(summary = "批量分配角色给用户")
  @PostMapping("/batch/roles")
  @org.springframework.web.bind.annotation.ResponseStatus(
      org.springframework.http.HttpStatus.NO_CONTENT)
  public void batchAssignRoles(@Valid @RequestBody BatchUserRoleCommand command) {
    userRoleApplicationService.batchAssignRoles(command);
  }

  @Operation(summary = "获取当前用户权限列表")
  @GetMapping("/permissions")
  public List<String> getPermissions() {
    Long userId = SecurityUtils.getUserId();
    return menuApplicationService.getMenuPermsByUserId(userId);
  }
}
