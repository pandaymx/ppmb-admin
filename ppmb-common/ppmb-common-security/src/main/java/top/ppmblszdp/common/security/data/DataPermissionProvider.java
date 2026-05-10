package top.ppmblszdp.common.security.data;

/** 数据权限提供者接口. 供应用系统实现，以提供当前用户针对某项权限菜单的数据范围和关联部门. */
public interface DataPermissionProvider {

  /**
   * 获取当前用户的数据权限上下文.
   *
   * @param userId 当前用户ID
   * @param permission 菜单权限标识
   * @return 包含最大数据范围和部门ID集合的上下文对象
   */
  DataPermissionContext getPermissionContext(Long userId, String permission);
}
