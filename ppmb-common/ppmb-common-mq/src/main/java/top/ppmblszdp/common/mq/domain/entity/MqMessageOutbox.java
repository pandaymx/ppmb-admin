package top.ppmblszdp.common.mq.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;

/** Outbox Message Entity for Reliable Message Delivery. */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "sys_mq_outbox")
@SQLRestriction("del_flag = 0")
public class MqMessageOutbox extends BaseMainEntity {

  private static final long serialVersionUID = 1L;

  /** Type of event to provide context to the consumer. */
  @Column(name = "event_type", length = 100)
  private String eventType;

  /** The routing key or topic the message was originally sent to. */
  @Column(name = "topic", length = 100)
  private String topic;

  /** Exchange name. */
  @Column(name = "exchange", length = 100)
  private String exchange;

  /** Routing key. */
  @Column(name = "routing_key", length = 100)
  private String routingKey;

  /** The actual business data payload stored as JSON. */
  @Column(name = "payload", columnDefinition = "TEXT")
  private String payload;

  /** Status of the message. */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private OutboxStatus status = OutboxStatus.PENDING;

  /** Number of times the message has been retried. */
  @Column(name = "retry_count", nullable = false)
  private Integer retryCount = 0;

  /** Next scheduled time to retry sending the message. */
  @Column(name = "next_retry_time")
  private LocalDateTime nextRetryTime;

  /** Last error message encountered during publishing. */
  @Column(name = "last_error_message", columnDefinition = "TEXT")
  private String lastErrorMessage;
}
