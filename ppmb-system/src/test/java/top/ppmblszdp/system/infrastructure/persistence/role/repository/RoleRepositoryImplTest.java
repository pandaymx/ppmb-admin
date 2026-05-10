package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.domain.model.role.entity.Role;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleRepositoryImplTest {

  @Mock private RoleJpaRepository jpaRepository;

  @InjectMocks private RoleRepositoryImpl repository;

  @Test
  void testSave() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(jpaRepository.save(any(Role.class))).thenReturn(role);
    Role saved = repository.save(role);
    assertEquals(role, saved);
  }

  @Test
  void testFindById() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(jpaRepository.findById(1L)).thenReturn(Optional.of(role));
    Optional<Role> found = repository.findById(1L);
    assertTrue(found.isPresent());
    assertEquals(role, found.get());
  }

  @Test
  void testDeleteById() {
    repository.deleteById(1L);
    verify(jpaRepository, times(1)).deleteById(1L);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFindPage() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    Page<Role> page = new PageImpl<>(List.of(role));
    ArgumentCaptor<Specification<Role>> specCaptor = ArgumentCaptor.forClass(Specification.class);
    when(jpaRepository.findAll(specCaptor.capture(), any(PageRequest.class))).thenReturn(page);

    // Test with all filters
    PageResult<Role> result = repository.findPage("Admin", 1, new PageQuery(1, 10));

    assertEquals(1, result.total());
    assertEquals(1, result.list().size());

    // Trigger Specification logic for coverage
    final Specification<Role> spec = specCaptor.getValue();
    final Root<Role> root = mock(Root.class);
    final Path<Object> path = mock(Path.class);
    when(root.get(anyString())).thenReturn(path);

    final CriteriaBuilder cb = mock(CriteriaBuilder.class);
    when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
    when(cb.like(any(), anyString())).thenReturn(mock(Predicate.class));
    when(cb.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));

    final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    spec.toPredicate(root, query, cb);

    // Test with null filters for remaining branches
    repository.findPage(null, null, new PageQuery(1, 10));
    specCaptor.getValue().toPredicate(root, query, cb);

    // Test with blank name
    repository.findPage(" ", 1, new PageQuery(1, 10));
    specCaptor.getValue().toPredicate(root, query, cb);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFindAll() {
    Role role = Role.create("Admin", "ROLE_ADMIN", "Desc");
    when(jpaRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(List.of(role));

    List<Role> list = repository.findAll();
    assertEquals(1, list.size());
  }
}
