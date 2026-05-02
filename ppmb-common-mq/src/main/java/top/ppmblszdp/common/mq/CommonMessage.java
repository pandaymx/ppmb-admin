package top.ppmblszdp.common.mq;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common Message Wrapper for standardizing MQ events across microservices.
 *
 * @param <T> the type of the message payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonMessage<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique message ID for idempotency checking. */
  @Builder.Default private String messageId = UUID.randomUUID().toString();

  /** Message creation timestamp. */
  @Builder.Default private Long timestamp = Instant.now().toEpochMilli();

  /** Type of event to provide context to the consumer. */
  private String eventType;

  /**
   * The routing key or topic the message was originally sent to (optional, but helpful for
   * tracing).
   */
  private String topic;

  /** The actual business data payload. */
  private T payload;
}
