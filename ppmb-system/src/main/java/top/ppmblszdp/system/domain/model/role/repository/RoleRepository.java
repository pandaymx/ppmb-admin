package top.ppmblszdp.system.domain.model.role.repository;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.domain.model.role.entity.Role;

/** 角色仓储接口. */
public interface RoleRepository {

  /** 保存或更新角色. */
  Role save(Role role);

  /** 根据 ID 查询角色. */
  Optional<Role> findById(Long id);

  /** 根据 ID 删除角色. */
  void deleteById(Long id);

  /** 分页查询角色. */
  PageResult<Role> findPage(String name, Integer status, PageQuery pageQuery);

  /** 查询所有可用角色列表. */
  List<Role> findAll();
}
