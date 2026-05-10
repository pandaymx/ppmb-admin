package top.ppmblszdp.common.security.data;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import top.ppmblszdp.common.security.data.enums.DataScope;

/**
 * 数据权限 JPA Specification.
 *
 * @param <T> 实体类型
 */
public class DataPermissionSpecification<T> implements Specification<T> {

  private final Specification<T> originalSpecification;

  /**
   * 包装原有的 Specification，附加数据权限过滤条件.
   *
   * @param originalSpecification 原有的 Specification
   */
  public DataPermissionSpecification(@Nullable Specification<T> originalSpecification) {
    this.originalSpecification = originalSpecification;
  }

  @Override
  @Nullable public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate originalPredicate = null;
    if (originalSpecification != null) {
      originalPredicate = originalSpecification.toPredicate(root, query, builder);
    }

    Predicate dataPermissionPredicate = buildDataPermissionPredicate(root, builder);

    if (originalPredicate == null) {
      return dataPermissionPredicate;
    }
    if (dataPermissionPredicate == null) {
      return originalPredicate;
    }

    return builder.and(originalPredicate, dataPermissionPredicate);
  }

  private Predicate buildDataPermissionPredicate(Root<T> root, CriteriaBuilder builder) {
    DataPermissionContext context = DataPermissionContextHolder.get().orElse(null);
    if (context == null || context.getDataScope() == null) {
      return null;
    }

    DataScope dataScope = context.getDataScope();
    String deptAlias = context.getDeptAlias();
    String userAlias = context.getUserAlias();

    return switch (dataScope) {
      case ALL -> null;
      case CUSTOM, DEPT, DEPT_AND_CHILD -> {
        if (context.getDeptIds() == null || context.getDeptIds().isEmpty()) {
          // If the scope is dept restricted but there are no allowed depts,
          // create a guaranteed false predicate, e.g., 1 = 0.
          yield builder.equal(builder.literal(1), 0);
        }
        CriteriaBuilder.In<Long> inClause = builder.in(root.get(deptAlias));
        for (Long deptId : context.getDeptIds()) {
          inClause.value(deptId);
        }
        yield inClause;
      }
      case SELF -> builder.equal(root.get(userAlias), context.getUserId());
    };
  }
}
