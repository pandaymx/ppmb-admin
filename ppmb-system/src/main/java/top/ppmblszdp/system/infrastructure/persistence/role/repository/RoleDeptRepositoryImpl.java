package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.RoleDept;
import top.ppmblszdp.system.domain.model.role.repository.RoleDeptRepository;

/** 角色与部门关联仓储实现. */
@Repository
@RequiredArgsConstructor
public class RoleDeptRepositoryImpl implements RoleDeptRepository {

  private final RoleDeptJpaRepository jpaRepository;

  @Override
  public void saveAll(List<RoleDept> roleDepts) {
    jpaRepository.saveAll(roleDepts);
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    jpaRepository.deleteByRoleId(roleId);
  }

  @Override
  public List<Long> findDeptIdsByRoleId(Long roleId) {
    return jpaRepository.findTargetDeptIdByTargetRoleId(roleId);
  }

  @Override
  public List<Long> findDeptIdsByRoleIds(List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findTargetDeptIdByTargetRoleIdIn(roleIds);
  }
}
