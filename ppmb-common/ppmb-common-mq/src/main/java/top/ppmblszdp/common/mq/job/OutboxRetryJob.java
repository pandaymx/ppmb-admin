package top.ppmblszdp.common.mq.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.ppmblszdp.common.mq.CommonMessage;
import top.ppmblszdp.common.mq.domain.entity.MqMessageOutbox;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;
import top.ppmblszdp.common.mq.repository.MqMessageOutboxRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRetryJob {

  private static final int MAX_RETRIES = 5;

  private final MqMessageOutboxRepository outboxRepository;
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;
  private final ExecutorService outboxRetryExecutor;
  private final top.ppmblszdp.common.mq.config.OutboxProperties outboxProperties;

  @Async("outboxRetryExecutor")
  @Scheduled(fixedDelayString = "${ppmb.mq.outbox.retry-delay:30000}")
  public void retryFailedMessages() {
    LocalDateTime now = LocalDateTime.now();
    List<OutboxStatus> statuses = Arrays.asList(OutboxStatus.PENDING, OutboxStatus.FAILED);

    // 可配置的每批处理数量，降低并发压力
    List<MqMessageOutbox> messagesToRetry =
        outboxRepository.findPendingOrFailedMessagesForRetry(
            statuses, now, MAX_RETRIES, PageRequest.of(0, outboxProperties.getBatchSize()));

    if (messagesToRetry.isEmpty()) {
      return;
    }

    log.info("Found {} outbox messages to retry.", messagesToRetry.size());

    CountDownLatch latch = new CountDownLatch(messagesToRetry.size());
    for (MqMessageOutbox outbox : messagesToRetry) {
      outboxRetryExecutor.submit(
          () -> {
            try {
              processRetry(outbox);
            } finally {
              latch.countDown();
            }
          });
    }

    try {
      // 等待所有任务完成，最多等待 25 秒（避免与下一次调度冲突）
      boolean completed = latch.await(25, TimeUnit.SECONDS);
      if (!completed) {
        log.warn("Outbox retry batch timed out, some messages may not have been processed.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Outbox retry interrupted", e);
    }
  }

  private void processRetry(MqMessageOutbox outbox) {
    try {
      // Because our payload is generic json, we use a generic type like Object.
      // In Java 21+, we use String for simplicity or Serializable.
      Object payload = objectMapper.readValue(outbox.getPayload(), Object.class);

      // To properly reconstruct CommonMessage
      @SuppressWarnings({"unchecked", "rawtypes"})
      CommonMessage reconstructedMessage =
          CommonMessage.builder()
              .eventType(outbox.getEventType())
              .topic(outbox.getTopic())
              // Jackson maps generic objects to Map, List, String, Number, Boolean, which are
              // Serializable
              .payload((java.io.Serializable) payload)
              .build();

      rabbitTemplate.convertAndSend(
          outbox.getExchange(), outbox.getRoutingKey(), reconstructedMessage);

      handleSuccess(outbox);
    } catch (Exception e) {
      log.error("Failed to retry outbox message id: {}", outbox.getId(), e);
      handleFailure(outbox, e);
    }
  }

  protected void handleSuccess(MqMessageOutbox outbox) {
    outbox.setStatus(OutboxStatus.PUBLISHED);
    outboxRepository.save(outbox);
    log.info("Successfully retried outbox message id: {}", outbox.getId());
  }

  protected void handleFailure(MqMessageOutbox outbox, Exception e) {
    int nextRetryCount = outbox.getRetryCount() + 1;
    outbox.setRetryCount(nextRetryCount);
    outbox.setLastErrorMessage(e.getMessage());

    if (nextRetryCount >= MAX_RETRIES) {
      outbox.setStatus(OutboxStatus.DEAD);
    } else {
      outbox.setStatus(OutboxStatus.FAILED);
      outbox.setNextRetryTime(calculateNextRetryTime(nextRetryCount));
    }

    outboxRepository.save(outbox);
  }

  private LocalDateTime calculateNextRetryTime(int retryCount) {
    // Exponential backoff: 1m, 5m, 15m, 1h...
    int minutesToAdd;
    switch (retryCount) {
      case 1:
        minutesToAdd = 1;
        break;
      case 2:
        minutesToAdd = 5;
        break;
      case 3:
        minutesToAdd = 15;
        break;
      case 4:
        minutesToAdd = 60;
        break;
      default:
        minutesToAdd = 60 * 24; // 1 day
    }
    return LocalDateTime.now().plusMinutes(minutesToAdd);
  }
}
