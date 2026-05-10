package top.ppmblszdp.common.web.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import top.ppmblszdp.common.web.aspect.TenantFilterAspect;
import top.ppmblszdp.common.web.audit.AuditEntityListener;
import top.ppmblszdp.common.web.audit.HibernateEventRegistry;

/**
 * Common JPA/Hibernate configuration. Only activated when JPA is present and configured in the
 * application context.
 */
@AutoConfiguration
@ConditionalOnClass({EntityManager.class, EntityManagerFactory.class})
public class JpaCommonAutoConfiguration {

  @Bean
  @ConditionalOnBean(EntityManager.class)
  public TenantFilterAspect tenantFilterAspect(EntityManager entityManager) {
    return new TenantFilterAspect(entityManager);
  }

  @Bean
  public AuditEntityListener auditEntityListener(ApplicationEventPublisher eventPublisher) {
    return new AuditEntityListener(eventPublisher);
  }

  @Bean
  @ConditionalOnBean(EntityManagerFactory.class)
  public HibernateEventRegistry hibernateEventRegistry(
      EntityManagerFactory entityManagerFactory, AuditEntityListener auditEntityListener) {
    return new HibernateEventRegistry(entityManagerFactory, auditEntityListener);
  }
}
