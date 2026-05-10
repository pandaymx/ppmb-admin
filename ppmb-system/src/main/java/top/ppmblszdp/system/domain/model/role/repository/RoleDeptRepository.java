package top.ppmblszdp.system.domain.model.role.repository;

import java.util.List;
import top.ppmblszdp.system.domain.model.role.entity.RoleDept;

/** 角色与部门关联仓储接口. */
public interface RoleDeptRepository {

  /**
   * 批量保存关联.
   *
   * @param roleDepts 关联列表
   */
  void saveAll(List<RoleDept> roleDepts);

  /**
   * 根据角色ID删除关联.
   *
   * @param roleId 角色ID
   */
  void deleteByRoleId(Long roleId);

  /**
   * 根据角色ID查找关联的部门ID列表.
   *
   * @param roleId 角色ID
   * @return 部门ID列表
   */
  List<Long> findDeptIdsByRoleId(Long roleId);

  /**
   * 根据多个角色ID查找关联的部门ID列表.
   *
   * @param roleIds 角色ID列表
   * @return 部门ID列表
   */
  List<Long> findDeptIdsByRoleIds(List<Long> roleIds);
}
