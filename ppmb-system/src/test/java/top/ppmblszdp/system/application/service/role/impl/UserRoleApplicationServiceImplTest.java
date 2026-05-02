package top.ppmblszdp.system.application.service.role.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.interfaces.web.role.dto.BatchUserRoleCommand;

@ExtendWith(MockitoExtension.class)
class UserRoleApplicationServiceImplTest {

  @Mock private UserRoleRepository userRoleRepository;

  @InjectMocks private UserRoleApplicationServiceImpl service;

  @Test
  void testGetUserRoles() {
    UserRole role = UserRole.create(1L, 2L);
    when(userRoleRepository.findByUserId(1L)).thenReturn(List.of(role));
    List<Long> roles = service.getUserRoles(1L);
    assertEquals(1, roles.size());
    assertEquals(2L, roles.get(0));
  }

  @Test
  void testAssignRolesToUser() {
    service.assignRolesToUser(1L, List.of(2L, 3L));
    verify(userRoleRepository, times(1)).deleteByUserId(1L);
    verify(userRoleRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testBatchAssignRoles() {
    BatchUserRoleCommand command = new BatchUserRoleCommand(List.of(1L, 2L), List.of(3L, 4L));
    service.batchAssignRoles(command);
    verify(userRoleRepository, times(1)).deleteByUserId(1L);
    verify(userRoleRepository, times(1)).deleteByUserId(2L);
    verify(userRoleRepository, times(1)).saveAll(anyList());
  }
}
