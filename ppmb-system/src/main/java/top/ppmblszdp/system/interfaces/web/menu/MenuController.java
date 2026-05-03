package top.ppmblszdp.system.interfaces.web.menu;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.security.util.SecurityUtils;
import top.ppmblszdp.system.application.service.menu.MenuApplicationService;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.RouterVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuApplicationService menuApplicationService;

  @PostMapping
  public Result<MenuDto> createMenu(@Valid @RequestBody CreateMenuCommand command) {
    return Result.success(menuApplicationService.createMenu(command));
  }

  @PutMapping("/{id}")
  public Result<MenuDto> updateMenu(
      @PathVariable Long id, @Valid @RequestBody UpdateMenuCommand command) {
    return Result.success(menuApplicationService.updateMenu(id, command));
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteMenu(@PathVariable Long id) {
    menuApplicationService.deleteMenu(id);
    return Result.success();
  }

  @GetMapping("/{id}")
  public Result<MenuDto> getMenuById(@PathVariable Long id) {
    return Result.success(menuApplicationService.getMenuById(id));
  }

  @GetMapping("/tree")
  public Result<List<MenuDto>> getMenuTree() {
    return Result.success(menuApplicationService.getMenuTree());
  }

  @GetMapping("/routers")
  public Result<List<RouterVo>> getRouters() {
    Long userId = SecurityUtils.getUserId();
    return Result.success(menuApplicationService.getRouters(userId));
  }
}
