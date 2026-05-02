package top.ppmblszdp.system.domain.model.role.repository;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.domain.model.role.entity.Role;

/** 角色仓储接口. */
public interface RoleRepository {

  /**
   * 保存或更新角色.
   *
   * @param role 角色实体
   * @return 保存后的角色实体
   */
  Role save(Role role);

  /**
   * 根据 ID 查询角色.
   *
   * @param id 角色 ID
   * @return 角色实体
   */
  Optional<Role> findById(Long id);

  /**
   * 根据 ID 删除角色.
   *
   * @param id 角色 ID
   */
  void deleteById(Long id);

  /**
   * 分页查询角色.
   *
   * @param name 角色名称 (模糊查询)
   * @param status 状态
   * @param pageQuery 分页参数
   * @return 分页结果
   */
  PageResult<Role> findPage(String name, Integer status, PageQuery pageQuery);

  /**
   * 查询所有可用角色列表.
   *
   * @return 角色列表
   */
  List<Role> findAll();
}
