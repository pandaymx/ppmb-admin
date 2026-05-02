package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;

public interface DictDataJpaRepository extends JpaRepository<DictData, Long> {
  Optional<DictData> findByDictTypeAndDictValue(String dictType, String dictValue);

  List<DictData> findByDictTypeAndStatusOrderByDictSortAsc(String dictType, Integer status);

  void deleteByParentId(Long parentId);

  long countByParentId(Long parentId);

  Page<DictData> findByDictType(String dictType, Pageable pageable);

  @Modifying
  @Query("update DictData set dictType = :newDictType where dictType = :oldDictType")
  void updateDictType(String oldDictType, String newDictType);
}
