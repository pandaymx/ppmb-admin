package top.ppmblszdp.system.application.service.menu;

import java.util.List;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.RouterVo;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

public interface MenuApplicationService {
  MenuDto createMenu(CreateMenuCommand command);

  MenuDto updateMenu(Long id, UpdateMenuCommand command);

  void deleteMenu(Long id);

  MenuDto getMenuById(Long id);

  List<MenuDto> getMenuList();

  List<MenuDto> getMenuTree();

  List<RouterVo> getRouters(Long userId);

  List<String> getMenuPermsByUserId(Long userId);
}
