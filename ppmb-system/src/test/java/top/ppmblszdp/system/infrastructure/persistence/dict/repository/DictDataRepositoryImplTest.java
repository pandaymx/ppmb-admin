package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import top.ppmblszdp.system.domain.model.dict.entity.DictData;

@ExtendWith(MockitoExtension.class)
class DictDataRepositoryImplTest {

  @Mock private DictDataJpaRepository jpaRepository;

  @InjectMocks private DictDataRepositoryImpl repository;

  @Test
  void testSave() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");
    when(jpaRepository.save(any(DictData.class))).thenReturn(data);

    DictData saved = repository.save(data);

    assertEquals(data, saved);
    verify(jpaRepository).save(data);
  }

  @Test
  void testFindById() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");
    when(jpaRepository.findById(1L)).thenReturn(Optional.of(data));

    Optional<DictData> found = repository.findById(1L);

    assertTrue(found.isPresent());
    assertEquals(data, found.get());
  }

  @Test
  void testFindByIdNotFound() {
    when(jpaRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<DictData> found = repository.findById(1L);

    assertTrue(found.isEmpty());
  }

  @Test
  void testFindByDictTypeAndDictValue() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");
    when(jpaRepository.findByDictTypeAndDictValue("user_status", "1"))
        .thenReturn(Optional.of(data));

    Optional<DictData> found = repository.findByDictTypeAndDictValue("user_status", "1");

    assertTrue(found.isPresent());
    assertEquals(data, found.get());
  }

  @Test
  void testFindByDictTypeAndDictValueNotFound() {
    when(jpaRepository.findByDictTypeAndDictValue("user_status", "999"))
        .thenReturn(Optional.empty());

    Optional<DictData> found = repository.findByDictTypeAndDictValue("user_status", "999");

    assertTrue(found.isEmpty());
  }

  @Test
  void testFindByDictTypeAndStatusOrderByDictSortAsc() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");
    when(jpaRepository.findByDictTypeAndStatusOrderByDictSortAsc("user_status", 0))
        .thenReturn(List.of(data));

    List<DictData> list = repository.findByDictTypeAndStatusOrderByDictSortAsc("user_status", 0);

    assertEquals(1, list.size());
    assertEquals(data, list.get(0));
  }

  @Test
  void testDeleteById() {
    repository.deleteById(1L);

    verify(jpaRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteByParentId() {
    repository.deleteByParentId(1L);

    verify(jpaRepository, times(1)).deleteByParentId(1L);
  }

  @Test
  void testCountByParentId() {
    when(jpaRepository.countByParentId(1L)).thenReturn(5L);

    long count = repository.countByParentId(1L);

    assertEquals(5L, count);
  }

  @Test
  void testFindByDictType() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");
    Page<DictData> page = new PageImpl<>(List.of(data));
    when(jpaRepository.findByDictType(eq("user_status"), any(Pageable.class))).thenReturn(page);

    Page<DictData> result = repository.findByDictType("user_status", PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
  }

  @Test
  void testUpdateDictType() {
    repository.updateDictType("old_type", "new_type");

    verify(jpaRepository, times(1)).updateDictType("old_type", "new_type");
  }
}
