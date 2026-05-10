package top.ppmblszdp.system.domain.model.role.repository;

import java.util.List;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;

/** 用户角色关联仓储接口. */
public interface UserRoleRepository {

  /**
   * 批量保存用户角色关联.
   *
   * @param userRoles 用户角色关联列表
   */
  void saveAll(List<UserRole> userRoles);

  /**
   * 根据用户ID删除关联.
   *
   * @param userId 用户ID
   */
  void deleteByUserId(Long userId);

  /**
   * 根据角色ID删除关联.
   *
   * @param roleId 角色ID
   */
  void deleteByRoleId(Long roleId);

  /**
   * 根据用户ID查询关联列表.
   *
   * @param userId 用户ID
   * @return 关联列表
   */
  List<UserRole> findByUserId(Long userId);

  /**
   * 统计使用该角色的用户数量.
   *
   * @param roleId 角色ID
   * @return 用户数量
   */
  long countByRoleId(Long roleId);

  /**
   * 根据用户ID查找关联的角色ID列表.
   *
   * @param userId 用户ID
   * @return 角色ID列表
   */
  List<Long> findRoleIdsByUserId(Long userId);
}
