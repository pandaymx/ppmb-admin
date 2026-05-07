package top.ppmblszdp.system.domain.model.menu.repository;

import java.util.List;
import java.util.Optional;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;

public interface MenuRepository {
  SysMenu save(SysMenu menu);

  Optional<SysMenu> findById(Long id);

  void deleteById(Long id);

  List<SysMenu> findAll();

  List<SysMenu> findAllByOrderByOrderNumAsc();

  List<SysMenu> findByIdIn(List<Long> ids);

  List<SysMenu> findByUserIdWithJoin(Long userId);
}
