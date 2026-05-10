package top.ppmblszdp.common.web.aspect;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.tenant.GlobalTable;
import top.ppmblszdp.common.tenant.TenantContextHolder;

/**
 * Aspect to automatically enable Hibernate filter for tenant isolation on Spring Data JPA
 * Repositories.
 */
@Aspect
public class TenantFilterAspect {

  private final EntityManager entityManager;

  public TenantFilterAspect(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
  public Object enableTenantFilter(ProceedingJoinPoint pjp) throws Throwable {
    Session session = entityManager.unwrap(Session.class);
    boolean filterEnabled =
        TenantContextHolder.get()
            .map(
                tenantId -> {
                  Class<?> repositoryInterface = getRepositoryInterface(pjp.getTarget().getClass());
                  if (repositoryInterface != null) {
                    Class<?>[] typeArguments =
                        GenericTypeResolver.resolveTypeArguments(
                            repositoryInterface,
                            org.springframework.data.repository.Repository.class);
                    if (typeArguments != null && typeArguments.length > 0) {
                      Class<?> entityClass = typeArguments[0];
                      if (entityClass != null
                          && !entityClass.isAnnotationPresent(GlobalTable.class)) {
                        if (session != null) {
                          session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
                          return true;
                        }
                      }
                    }
                  }
                  return false;
                })
            .orElse(false);

    try {
      return pjp.proceed();
    } finally {
      if (filterEnabled && session != null) {
        session.disableFilter("tenantFilter");
      }
    }
  }

  /** Helper method to find the repository interface from a proxy target class. */
  private Class<?> getRepositoryInterface(Class<?> targetClass) {
    for (Class<?> iface : targetClass.getInterfaces()) {
      if (org.springframework.data.repository.Repository.class.isAssignableFrom(iface)) {
        return iface;
      }
    }
    return null;
  }
}
