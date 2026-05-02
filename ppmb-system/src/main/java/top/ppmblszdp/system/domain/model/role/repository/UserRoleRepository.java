package top.ppmblszdp.system.domain.model.role.repository;

import java.util.List;
import top.ppmblszdp.system.domain.model.role.entity.UserRole;

/** 用户角色关联仓储接口. */
public interface UserRoleRepository {

  /** 批量保存用户角色关联. */
  List<UserRole> saveAll(List<UserRole> userRoles);

  /** 根据用户 ID 查询关联列表. */
  List<UserRole> findByUserId(Long userId);

  /** 根据用户 ID 列表查询关联列表. */
  List<UserRole> findByUserIds(List<Long> userIds);

  /** 根据用户 ID 删除关联. */
  void deleteByUserId(Long userId);

  /** 统计角色关联的用户数量. */
  long countByRoleId(Long roleId);
}
