package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.dict.entity.DictType;
import top.ppmblszdp.system.domain.model.dict.repository.DictTypeRepository;

@Repository
@RequiredArgsConstructor
public class DictTypeRepositoryImpl implements DictTypeRepository {
  private final DictTypeJpaRepository jpaRepository;

  @Override
  public DictType save(DictType dictType) {
    return jpaRepository.save(dictType);
  }

  @Override
  public Optional<DictType> findById(Long id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<DictType> findByDictType(String dictType) {
    return jpaRepository.findByDictType(dictType);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public Page<DictType> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable);
  }
}
