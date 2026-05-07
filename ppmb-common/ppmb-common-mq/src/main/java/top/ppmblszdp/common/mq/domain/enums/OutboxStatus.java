package top.ppmblszdp.common.mq.domain.enums;

/** Status of an outbox message. */
public enum OutboxStatus {
  /** Pending to be published. */
  PENDING,
  /** Successfully published to MQ. */
  PUBLISHED,
  /** Failed to publish to MQ. */
  FAILED,
  /** Dead after max retries. */
  DEAD
}
