package top.ppmblszdp.system.application.service.role;

import java.util.List;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.interfaces.web.role.dto.CreateRoleCommand;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;
import top.ppmblszdp.system.interfaces.web.role.dto.RolePageQuery;
import top.ppmblszdp.system.interfaces.web.role.dto.UpdateRoleCommand;

public interface RoleApplicationService {
  RoleDto createRole(CreateRoleCommand command);

  RoleDto updateRole(Long id, UpdateRoleCommand command);

  void deleteRole(Long id);

  PageResult<RoleDto> getRolePage(RolePageQuery query, PageQuery pageQuery);

  List<RoleDto> getRoleOptions();
}
