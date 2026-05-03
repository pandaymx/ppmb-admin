package top.ppmblszdp.system.infrastructure.persistence.menu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.menu.entity.SysRoleMenu;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;

@Repository
public interface RoleMenuJpaRepository
    extends JpaRepository<SysRoleMenu, Long>, RoleMenuRepository {
  void deleteByRoleId(Long roleId);

  List<SysRoleMenu> findByRoleId(Long roleId);

  List<SysRoleMenu> findByRoleIdIn(List<Long> roleIds);
}
