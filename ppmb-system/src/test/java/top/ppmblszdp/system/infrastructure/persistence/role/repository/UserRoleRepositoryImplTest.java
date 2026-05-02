package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class UserRoleRepositoryImplTest {

  @Mock private UserRoleJpaRepository jpaRepository;

  @InjectMocks private UserRoleRepositoryImpl repository;

  @Test
  void testSaveAll() {
    List<UserRole> roles = List.of(UserRole.create(1L, 2L));
    when(jpaRepository.saveAll(roles)).thenReturn(roles);
    List<UserRole> saved = repository.saveAll(roles);
    assertEquals(1, saved.size());
  }

  @Test
  void testFindByUserId() {
    List<UserRole> roles = List.of(UserRole.create(1L, 2L));
    when(jpaRepository.findByUserId(1L)).thenReturn(roles);
    List<UserRole> found = repository.findByUserId(1L);
    assertEquals(1, found.size());
  }

  @Test
  void testFindByUserIds() {
    List<UserRole> roles = List.of(UserRole.create(1L, 2L));
    when(jpaRepository.findByUserIdIn(List.of(1L))).thenReturn(roles);
    List<UserRole> found = repository.findByUserIds(List.of(1L));
    assertEquals(1, found.size());
  }

  @Test
  void testDeleteByUserId() {
    repository.deleteByUserId(1L);
    verify(jpaRepository, times(1)).deleteByUserId(1L);
  }

  @Test
  void testCountByRoleId() {
    when(jpaRepository.countByRoleId(1L)).thenReturn(5L);
    long count = repository.countByRoleId(1L);
    assertEquals(5L, count);
  }
}
