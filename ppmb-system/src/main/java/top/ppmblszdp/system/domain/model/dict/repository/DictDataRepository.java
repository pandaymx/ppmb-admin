package top.ppmblszdp.system.domain.model.dict.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;

public interface DictDataRepository {
  DictData save(DictData dictData);

  Optional<DictData> findById(Long id);

  Optional<DictData> findByDictTypeAndDictValue(String dictType, String dictValue);

  List<DictData> findByDictTypeAndStatusOrderByDictSortAsc(String dictType, Integer status);

  void deleteById(Long id);

  void deleteByParentId(Long parentId);

  long countByParentId(Long parentId);

  Page<DictData> findByDictType(String dictType, Pageable pageable);

  void updateDictType(String oldDictType, String newDictType);
}
