package top.ppmblszdp.system.application.service.role;

import java.util.List;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;

public interface UserRoleApplicationService {
  List<Long> getUserRoles(Long userId);

  void assignRolesToUser(Long userId, List<Long> roleIds);

  void batchAssignRoles(BatchUserRoleCommand command);
}
