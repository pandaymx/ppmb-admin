package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;

/** 用户角色关联 JPA 仓储. */
@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRole, Long> {

  /**
   * 根据用户ID物理删除关联.
   *
   * @param userId 用户ID
   */
  @Modifying
  @Query("DELETE FROM UserRole ur WHERE ur.targetUserId = :userId")
  void deleteByTargetUserId(@Param("userId") Long userId);

  /**
   * 根据角色ID物理删除关联.
   *
   * @param roleId 角色ID
   */
  @Modifying
  @Query("DELETE FROM UserRole ur WHERE ur.targetRoleId = :roleId")
  void deleteByTargetRoleId(@Param("roleId") Long roleId);

  /**
   * 根据用户ID查询关联.
   *
   * @param userId 用户ID
   * @return 关联列表
   */
  List<UserRole> findByTargetUserId(Long userId);

  /**
   * 统计使用该角色的关联数量.
   *
   * @param roleId 角色ID
   * @return 数量
   */
  long countByTargetRoleId(Long roleId);

  /**
   * 根据用户ID查找关联的角色ID列表.
   *
   * @param userId 用户ID
   * @return 角色ID列表
   */
  @Query("SELECT ur.targetRoleId FROM UserRole ur WHERE ur.targetUserId = :userId")
  List<Long> findTargetRoleIdByTargetUserId(@Param("userId") Long userId);
}
