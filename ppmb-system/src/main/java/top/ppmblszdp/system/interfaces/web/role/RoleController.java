package top.ppmblszdp.system.interfaces.web.role;

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
import top.ppmblszdp.system.application.service.role.RoleApplicationService;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

  private final RoleApplicationService roleApplicationService;

  @PostMapping
  public Result<RoleDto> createRole(@Valid @RequestBody CreateRoleCommand command) {
    return Result.success(roleApplicationService.createRole(command));
  }

  @PutMapping("/{id}")
  public Result<RoleDto> updateRole(
      @PathVariable Long id, @Valid @RequestBody UpdateRoleCommand command) {
    return Result.success(roleApplicationService.updateRole(id, command));
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteRole(@PathVariable Long id) {
    roleApplicationService.deleteRole(id);
    return Result.success();
  }

  @GetMapping
  public Result<PageResult<RoleDto>> getRolePage(RolePageQuery query, PageQuery pageQuery) {
    return Result.success(roleApplicationService.getRolePage(query, pageQuery));
  }

  @GetMapping("/options")
  public Result<List<RoleDto>> getRoleOptions() {
    return Result.success(roleApplicationService.getRoleOptions());
  }
}
