package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.RoleDept;

/** 角色与部门关联 JPA 仓储. */
@Repository
public interface RoleDeptJpaRepository extends JpaRepository<RoleDept, Long> {

  /**
   * 根据角色ID删除关联.
   *
   * @param roleId 角色ID
   */
  @Modifying
  @Query("UPDATE RoleDept rd SET rd.delFlag = 1 WHERE rd.targetRoleId = :roleId")
  void deleteByRoleId(@Param("roleId") Long roleId);

  /**
   * 根据角色ID查找关联的部门ID列表.
   *
   * @param roleId 角色ID
   * @return 部门ID列表
   */
  @Query("SELECT rd.targetDeptId FROM RoleDept rd WHERE rd.targetRoleId = :roleId")
  List<Long> findTargetDeptIdByTargetRoleId(@Param("roleId") Long roleId);

  /**
   * 根据多个角色ID查找关联的部门ID列表.
   *
   * @param roleIds 角色ID列表
   * @return 部门ID列表
   */
  @Query("SELECT rd.targetDeptId FROM RoleDept rd WHERE rd.targetRoleId IN :roleIds")
  List<Long> findTargetDeptIdByTargetRoleIdIn(@Param("roleIds") List<Long> roleIds);
}
