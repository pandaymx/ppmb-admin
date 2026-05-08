package top.ppmblszdp.common.web.audit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Hibernate 事件注册测试")
class HibernateEventRegistryTest {

  @Mock private EntityManagerFactory entityManagerFactory;
  @Mock private AuditEntityListener auditEntityListener;
  @Mock private SessionFactoryImplementor sessionFactory;
  @Mock private ServiceRegistryImplementor serviceRegistry;
  @Mock private EventListenerRegistry eventListenerRegistry;

  @Test
  @DisplayName("应注册实体增删改事件监听器")
  void shouldRegisterAuditEntityListener() {
    when(entityManagerFactory.unwrap(SessionFactoryImplementor.class)).thenReturn(sessionFactory);
    when(sessionFactory.getServiceRegistry()).thenReturn(serviceRegistry);
    when(serviceRegistry.getService(EventListenerRegistry.class)).thenReturn(eventListenerRegistry);

    HibernateEventRegistry registry =
        new HibernateEventRegistry(entityManagerFactory, auditEntityListener);
    registry.registerListeners();

    verify(eventListenerRegistry).appendListeners(EventType.POST_INSERT, auditEntityListener);
    verify(eventListenerRegistry).appendListeners(EventType.POST_UPDATE, auditEntityListener);
    verify(eventListenerRegistry).appendListeners(EventType.POST_DELETE, auditEntityListener);
  }
}
