package top.ppmblszdp.common.web.audit;

import com.fasterxml.jackson.databind.json.JsonMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.api.annotation.Auditable;
import top.ppmblszdp.common.api.dto.AuditLogMessage;
import top.ppmblszdp.common.api.event.AuditLogEvent;
import top.ppmblszdp.common.security.util.SecurityUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEntityListener
    implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

  private final ApplicationEventPublisher eventPublisher;
  private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

  @Override
  public void onPostInsert(PostInsertEvent event) {
    if (event.getEntity().getClass().isAnnotationPresent(Auditable.class)) {
      publishEvent(
          "INSERT", event.getEntity(), event.getId(), null, event.getState(), event.getPersister());
    }
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    if (event.getEntity().getClass().isAnnotationPresent(Auditable.class)) {
      publishEvent(
          "UPDATE",
          event.getEntity(),
          event.getId(),
          event.getOldState(),
          event.getState(),
          event.getPersister());
    }
  }

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    if (event.getEntity().getClass().isAnnotationPresent(Auditable.class)) {
      publishEvent(
          "DELETE",
          event.getEntity(),
          event.getId(),
          event.getDeletedState(),
          null,
          event.getPersister());
    }
  }

  @Override
  public boolean requiresPostCommitHandling(EntityPersister persister) {
    return false;
  }

  private void publishEvent(
      String operation,
      Object entity,
      Object id,
      Object[] oldState,
      Object[] newState,
      EntityPersister persister) {
    try {
      String[] propertyNames = persister.getPropertyNames();
      String oldJson = null;
      String newJson = null;

      if (oldState != null) {
        Map<String, Object> oldMap = new HashMap<>();
        for (int i = 0; i < propertyNames.length; i++) {
          if (!persister.getPropertyTypes()[i].isAssociationType()) {
            oldMap.put(propertyNames[i], oldState[i]);
          }
        }
        EntityStateRecord oldRecord = new EntityStateRecord(oldMap);
        oldJson = jsonMapper.writeValueAsString(oldRecord.state());
      }

      if (newState != null) {
        Map<String, Object> newMap = new HashMap<>();
        for (int i = 0; i < propertyNames.length; i++) {
          if (!persister.getPropertyTypes()[i].isAssociationType()) {
            newMap.put(propertyNames[i], newState[i]);
          }
        }
        EntityStateRecord newRecord = new EntityStateRecord(newMap);
        newJson = jsonMapper.writeValueAsString(newRecord.state());
      }

      AuditLogMessage message =
          new AuditLogMessage(
              UUID.randomUUID().toString(),
              operation,
              entity.getClass().getSimpleName(),
              String.valueOf(id),
              oldJson,
              newJson,
              null,
              null,
              null,
              null,
              SecurityUtils.getUserId(),
              top.ppmblszdp.common.tenant.TenantContextHolder.get().orElse(null),
              LocalDateTime.now());

      eventPublisher.publishEvent(new AuditLogEvent(this, message));
    } catch (Exception e) {
      log.error(
          "Failed to process audit event for entity {}", entity.getClass().getSimpleName(), e);
    }
  }
}
