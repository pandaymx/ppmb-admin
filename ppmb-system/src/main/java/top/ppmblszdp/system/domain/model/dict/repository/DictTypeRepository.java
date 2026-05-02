package top.ppmblszdp.system.domain.model.dict.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;

public interface DictTypeRepository {
  DictType save(DictType dictType);

  Optional<DictType> findById(Long id);

  Optional<DictType> findByDictType(String dictType);

  void deleteById(Long id);

  Page<DictType> findAll(Pageable pageable);
}
