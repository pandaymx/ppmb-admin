package top.ppmblszdp.system.infrastructure.persistence.dict.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import top.ppmblszdp.system.domain.model.dict.entity.DictType;

@ExtendWith(MockitoExtension.class)
class DictTypeRepositoryImplTest {

  @Mock private DictTypeJpaRepository jpaRepository;

  @InjectMocks private DictTypeRepositoryImpl repository;

  @Test
  void testSave() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");
    when(jpaRepository.save(any(DictType.class))).thenReturn(dictType);

    DictType saved = repository.save(dictType);

    assertEquals(dictType, saved);
    verify(jpaRepository).save(dictType);
  }

  @Test
  void testFindById() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");
    when(jpaRepository.findById(1L)).thenReturn(Optional.of(dictType));

    Optional<DictType> found = repository.findById(1L);

    assertTrue(found.isPresent());
    assertEquals(dictType, found.get());
  }

  @Test
  void testFindByIdNotFound() {
    when(jpaRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<DictType> found = repository.findById(1L);

    assertTrue(found.isEmpty());
  }

  @Test
  void testFindByDictType() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");
    when(jpaRepository.findByDictType("user_status")).thenReturn(Optional.of(dictType));

    Optional<DictType> found = repository.findByDictType("user_status");

    assertTrue(found.isPresent());
    assertEquals(dictType, found.get());
  }

  @Test
  void testFindByDictTypeNotFound() {
    when(jpaRepository.findByDictType("not_exist")).thenReturn(Optional.empty());

    Optional<DictType> found = repository.findByDictType("not_exist");

    assertTrue(found.isEmpty());
  }

  @Test
  void testDeleteById() {
    repository.deleteById(1L);

    verify(jpaRepository, times(1)).deleteById(1L);
  }

  @Test
  void testFindAll() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");
    Page<DictType> page = new PageImpl<>(java.util.List.of(dictType));
    when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);

    Page<DictType> result = repository.findAll(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
  }
}
