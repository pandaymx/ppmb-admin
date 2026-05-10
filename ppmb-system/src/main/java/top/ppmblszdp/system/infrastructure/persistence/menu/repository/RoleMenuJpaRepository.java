package top.ppmblszdp.system.infrastructure.persistence.menu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;

@Repository
public interface RoleMenuJpaRepository extends JpaRepository<SysRoleMenu, Long> {
  List<SysRoleMenu> findByTargetRoleId(Long roleId);

  List<SysRoleMenu> findByTargetRoleIdIn(List<Long> roleIds);

  void deleteByTargetRoleId(Long roleId);

  @Query("SELECT rm.targetRoleId FROM SysRoleMenu rm JOIN SysMenu m ON rm.targetMenuId = m.id "
      + "WHERE m.perms = :permission")
  List<Long> findRoleIdsByMenuPerms(@Param("permission") String permission);
}
