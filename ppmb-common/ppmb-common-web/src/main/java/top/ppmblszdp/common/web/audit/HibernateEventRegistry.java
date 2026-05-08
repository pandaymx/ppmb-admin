package top.ppmblszdp.common.web.audit;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HibernateEventRegistry {

  private final EntityManagerFactory entityManagerFactory;
  private final AuditEntityListener auditEntityListener;

  @PostConstruct
  public void registerListeners() {
    SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
    EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

    if (registry != null) {
        registry.appendListeners(EventType.POST_INSERT, auditEntityListener);
        registry.appendListeners(EventType.POST_UPDATE, auditEntityListener);
        registry.appendListeners(EventType.POST_DELETE, auditEntityListener);
    }
  }
}
