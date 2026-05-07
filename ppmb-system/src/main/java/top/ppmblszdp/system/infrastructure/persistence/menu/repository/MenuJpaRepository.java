package top.ppmblszdp.system.infrastructure.persistence.menu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;

@Repository
public interface MenuJpaRepository
    extends JpaRepository<SysMenu, Long>, JpaSpecificationExecutor<SysMenu>, MenuRepository {
  List<SysMenu> findByIdIn(List<Long> ids);

  List<SysMenu> findAllByOrderByOrderNumAsc();

  @org.springframework.data.jpa.repository.Query(
      "SELECT DISTINCT m FROM SysMenu m "
          + "JOIN SysRoleMenu rm ON m.id = rm.menuId "
          + "JOIN UserRole ur ON rm.roleId = ur.roleId "
          + "WHERE ur.userId = :userId "
          + "ORDER BY m.orderNum ASC")
  List<SysMenu> findByUserIdWithJoin(Long userId);
}
