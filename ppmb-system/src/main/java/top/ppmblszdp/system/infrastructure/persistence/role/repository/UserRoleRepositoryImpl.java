package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

  private final UserRoleJpaRepository jpaRepository;

  @Override
  public void saveAll(List<UserRole> userRoles) {
    jpaRepository.saveAll(userRoles);
  }

  @Override
  public void deleteByUserId(Long userId) {
    jpaRepository.deleteByTargetUserId(userId);
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    jpaRepository.deleteByTargetRoleId(roleId);
  }

  @Override
  public List<UserRole> findByUserId(Long userId) {
    return jpaRepository.findByTargetUserId(userId);
  }

  @Override
  public long countByRoleId(Long roleId) {
    return jpaRepository.countByTargetRoleId(roleId);
  }

  @Override
  public List<Long> findRoleIdsByUserId(Long userId) {
    return jpaRepository.findTargetRoleIdByTargetUserId(userId);
  }
}
