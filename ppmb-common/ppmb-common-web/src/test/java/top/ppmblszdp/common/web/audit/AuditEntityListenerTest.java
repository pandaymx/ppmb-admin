package top.ppmblszdp.common.web.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import top.ppmblszdp.common.api.annotation.Auditable;
import top.ppmblszdp.common.api.event.AuditLogEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("实体审计监听器测试")
class AuditEntityListenerTest {

  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private PostInsertEvent postInsertEvent;
  @Mock private EntityPersister persister;
  @Mock private Type basicType;

  @Test
  @DisplayName("审计实体插入时应发布审计事件")
  void shouldPublishAuditEventOnInsertForAuditableEntity() {
    final AuditEntityListener listener = new AuditEntityListener(eventPublisher);
    AuditableEntity entity = new AuditableEntity();

    when(postInsertEvent.getEntity()).thenReturn(entity);
    when(postInsertEvent.getId()).thenReturn(1L);
    when(postInsertEvent.getState()).thenReturn(new Object[] {"Alice"});
    when(postInsertEvent.getPersister()).thenReturn(persister);
    when(persister.getPropertyNames()).thenReturn(new String[] {"name"});
    when(persister.getPropertyTypes()).thenReturn(new Type[] {basicType});
    when(basicType.isAssociationType()).thenReturn(false);

    listener.onPostInsert(postInsertEvent);

    ArgumentCaptor<AuditLogEvent> eventCaptor = ArgumentCaptor.forClass(AuditLogEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    AuditLogEvent event = eventCaptor.getValue();
    assertEquals("INSERT", event.getAuditLogMessage().operationName());
    assertEquals("AuditableEntity", event.getAuditLogMessage().entityName());
    assertEquals("1", event.getAuditLogMessage().entityId());
    assertNull(event.getAuditLogMessage().oldValue());
  }

  @Test
  @DisplayName("非审计实体插入时不应发布事件")
  void shouldNotPublishEventForNonAuditableEntity() {
    final AuditEntityListener listener = new AuditEntityListener(eventPublisher);
    NonAuditableEntity entity = new NonAuditableEntity();
    when(postInsertEvent.getEntity()).thenReturn(entity);

    listener.onPostInsert(postInsertEvent);

    verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("requiresPostCommitHandling 应返回 false")
  void shouldReturnFalseForPostCommitHandling() {
    final AuditEntityListener listener = new AuditEntityListener(eventPublisher);
    assertFalse(listener.requiresPostCommitHandling(persister));
  }

  @Auditable
  static class AuditableEntity {}

  static class NonAuditableEntity {}
}
