package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;

public interface DictTypeJpaRepository extends JpaRepository<DictType, Long> {
  Optional<DictType> findByDictType(String dictType);
}
