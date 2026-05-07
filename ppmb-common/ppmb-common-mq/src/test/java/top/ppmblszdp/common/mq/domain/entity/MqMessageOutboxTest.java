package top.ppmblszdp.common.mq.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;

@DisplayName("MqMessageOutbox 实体测试")
class MqMessageOutboxTest {

  @Test
  @DisplayName("测试实体属性的 Getter 和 Setter")
  void testGettersAndSetters() {
    MqMessageOutbox outbox = new MqMessageOutbox();
    LocalDateTime now = LocalDateTime.now();

    outbox.setExchange("exchange");
    outbox.setRoutingKey("routingKey");
    outbox.setTopic("topic");
    outbox.setEventType("eventType");
    outbox.setPayload("payload");
    outbox.setStatus(OutboxStatus.PENDING);
    outbox.setRetryCount(1);
    outbox.setNextRetryTime(now);
    outbox.setLastErrorMessage("error");

    assertThat(outbox.getExchange()).isEqualTo("exchange");
    assertThat(outbox.getRoutingKey()).isEqualTo("routingKey");
    assertThat(outbox.getTopic()).isEqualTo("topic");
    assertThat(outbox.getEventType()).isEqualTo("eventType");
    assertThat(outbox.getPayload()).isEqualTo("payload");
    assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PENDING);
    assertThat(outbox.getRetryCount()).isEqualTo(1);
    assertThat(outbox.getNextRetryTime()).isEqualTo(now);
    assertThat(outbox.getLastErrorMessage()).isEqualTo("error");
  }
}
