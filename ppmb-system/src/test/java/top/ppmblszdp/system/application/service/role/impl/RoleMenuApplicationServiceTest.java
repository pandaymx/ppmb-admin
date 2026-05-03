package top.ppmblszdp.system.application.service.role.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleMenuApplicationService 业务逻辑测试")
class RoleMenuApplicationServiceTest {

  @Mock private RoleMenuRepository roleMenuRepository;

  @InjectMocks private RoleMenuApplicationService roleMenuApplicationService;

  @Test
  @DisplayName("为角色分配菜单 - 正常情况")
  void shouldAssignMenusToRole() {
    Long roleId = 1L;
    List<Long> menuIds = List.of(10L, 11L);

    roleMenuApplicationService.assignMenusToRole(roleId, menuIds);

    verify(roleMenuRepository, times(1)).deleteByRoleId(roleId);
    verify(roleMenuRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("为角色分配菜单 - 空菜单列表")
  void shouldNotSaveWhenMenuIdsIsEmpty() {
    Long roleId = 1L;

    roleMenuApplicationService.assignMenusToRole(roleId, Collections.emptyList());

    verify(roleMenuRepository, times(1)).deleteByRoleId(roleId);
    verify(roleMenuRepository, times(0)).saveAll(anyList());
  }

  @Test
  @DisplayName("为角色分配菜单 - null 菜单列表")
  void shouldNotSaveWhenMenuIdsIsNull() {
    Long roleId = 1L;

    roleMenuApplicationService.assignMenusToRole(roleId, null);

    verify(roleMenuRepository, times(1)).deleteByRoleId(roleId);
    verify(roleMenuRepository, times(0)).saveAll(anyList());
  }
}
