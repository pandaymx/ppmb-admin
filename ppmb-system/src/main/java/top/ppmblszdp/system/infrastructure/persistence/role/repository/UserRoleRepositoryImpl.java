package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;

@Component
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

  private final UserRoleJpaRepository userRoleJpaRepository;

  @Override
  public List<UserRole> saveAll(List<UserRole> userRoles) {
    return userRoleJpaRepository.saveAll(userRoles);
  }

  @Override
  public List<UserRole> findByUserId(Long userId) {
    return userRoleJpaRepository.findByUserId(userId);
  }

  @Override
  public List<UserRole> findByUserIds(List<Long> userIds) {
    return userRoleJpaRepository.findByUserIdIn(userIds);
  }

  @Override
  public void deleteByUserId(Long userId) {
    userRoleJpaRepository.deleteByUserId(userId);
  }

  @Override
  public long countByRoleId(Long roleId) {
    return userRoleJpaRepository.countByRoleId(roleId);
  }
}
