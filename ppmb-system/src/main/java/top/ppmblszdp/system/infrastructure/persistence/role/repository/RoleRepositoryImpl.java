package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

  private final RoleJpaRepository roleJpaRepository;

  @Override
  public Role save(Role role) {
    return roleJpaRepository.save(role);
  }

  @Override
  public Optional<Role> findById(Long id) {
    return roleJpaRepository.findById(id);
  }

  @Override
  public void deleteById(Long id) {
    roleJpaRepository.deleteById(id);
  }

  @Override
  public PageResult<Role> findPage(String name, Integer status, PageQuery pageQuery) {
    Specification<Role> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(cb.equal(root.get("delFlag"), 0)); // Not deleted
          if (name != null && !name.isBlank()) {
            predicates.add(cb.like(root.get("roleName"), "%" + name + "%"));
          }
          if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    PageRequest pageRequest =
        PageRequest.of(
            pageQuery.pageNum() - 1,
            pageQuery.pageSize(),
            Sort.by(Sort.Direction.DESC, "createTime"));
    Page<Role> page = roleJpaRepository.findAll(spec, pageRequest);

    return PageResult.of(
        page.getTotalElements(), page.getContent(), pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public List<Role> findAll() {
    Specification<Role> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(cb.equal(root.get("delFlag"), 0)); // Not deleted
          predicates.add(cb.equal(root.get("status"), 1)); // Only enabled roles
          return cb.and(predicates.toArray(new Predicate[0]));
        };
    return roleJpaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
  }
}
