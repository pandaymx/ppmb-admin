package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;
import top.ppmblszdp.system.domain.model.dict.repository.DictDataRepository;

@Repository
@RequiredArgsConstructor
public class DictDataRepositoryImpl implements DictDataRepository {
  private final DictDataJpaRepository jpaRepository;

  @Override
  public DictData save(DictData dictData) {
    return jpaRepository.save(dictData);
  }

  @Override
  public Optional<DictData> findById(Long id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<DictData> findByDictTypeAndDictValue(String dictType, String dictValue) {
    return jpaRepository.findByDictTypeAndDictValue(dictType, dictValue);
  }

  @Override
  public List<DictData> findByDictTypeAndStatusOrderByDictSortAsc(String dictType, Integer status) {
    return jpaRepository.findByDictTypeAndStatusOrderByDictSortAsc(dictType, status);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void deleteByParentId(Long parentId) {
    jpaRepository.deleteByParentId(parentId);
  }

  @Override
  public long countByParentId(Long parentId) {
    return jpaRepository.countByParentId(parentId);
  }

  @Override
  public Page<DictData> findByDictType(String dictType, Pageable pageable) {
    return jpaRepository.findByDictType(dictType, pageable);
  }

  @Override
  public void updateDictType(String oldDictType, String newDictType) {
    jpaRepository.updateDictType(oldDictType, newDictType);
  }
}
