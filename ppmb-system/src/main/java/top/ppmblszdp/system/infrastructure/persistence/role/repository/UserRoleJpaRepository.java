package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;

/** 用户角色关联 JPA 仓储. */
@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRole, Long> {

  List<UserRole> findByUserId(Long userId);

  List<UserRole> findByUserIdIn(List<Long> userIds);

  void deleteByUserId(Long userId);

  long countByRoleId(Long roleId);
}
