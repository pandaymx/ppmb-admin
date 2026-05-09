package top.ppmblszdp.common.mq.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import top.ppmblszdp.common.mq.domain.entity.MqMessageOutbox;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;
import top.ppmblszdp.common.mq.repository.MqMessageOutboxRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Outbox 重试任务测试")
class OutboxRetryJobTest {

  @Mock private MqMessageOutboxRepository outboxRepository;
  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private ObjectMapper objectMapper;
  @Mock private ExecutorService outboxRetryExecutor;

  @InjectMocks private OutboxRetryJob outboxRetryJob;

  @Test
  @DisplayName("重试失败消息时应提交给执行器")
  void shouldSubmitToExecutorWhenRetryFailedMessages() {
    MqMessageOutbox outbox = new MqMessageOutbox();
    when(outboxRepository.findPendingOrFailedMessagesForRetry(
            anyList(), any(LocalDateTime.class), anyInt(), any(Pageable.class)))
        .thenReturn(List.of(outbox));

    outboxRetryJob.retryFailedMessages();

    verify(outboxRetryExecutor, times(1)).submit(any(Runnable.class));
  }

  @Test
  @DisplayName("处理重试成功时应更新状态为已发布")
  void shouldProcessRetrySuccessfully() throws Exception {
    MqMessageOutbox outbox = new MqMessageOutbox();
    outbox.setPayload("{\"data\":\"test\"}");
    outbox.setExchange("ex");
    outbox.setRoutingKey("rk");
    outbox.setEventType("event");
    outbox.setTopic("topic");

    outboxRetryJob.handleSuccess(outbox);

    assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
    verify(outboxRepository).save(outbox);
  }

  @Test
  @DisplayName("处理重试失败时应增加重试次数并根据阈值更新状态")
  void shouldHandleFailureCorrecty() {
    MqMessageOutbox outbox = new MqMessageOutbox();
    outbox.setRetryCount(0);

    outboxRetryJob.handleFailure(outbox, new RuntimeException("error"));

    assertThat(outbox.getRetryCount()).isEqualTo(1);
    assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.FAILED);
    assertThat(outbox.getNextRetryTime()).isNotNull();
    verify(outboxRepository).save(outbox);

    // Test all backoff branches
    for (int i = 1; i <= 5; i++) {
      outbox.setRetryCount(i);
      LocalDateTime next =
          ReflectionTestUtils.invokeMethod(outboxRetryJob, "calculateNextRetryTime", i);
      assertThat(next).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    // Test dead letter
    outbox.setRetryCount(4); // will become 5
    outboxRetryJob.handleFailure(outbox, new RuntimeException("error"));
    assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.DEAD);
  }

  @Test
  @DisplayName("测试 processRetry 逻辑")
  void testProcessRetry() throws Exception {
    MqMessageOutbox outbox = new MqMessageOutbox();
    outbox.setPayload("{\"data\":\"test\"}");
    outbox.setExchange("ex");
    outbox.setRoutingKey("rk");
    outbox.setEventType("event");
    outbox.setTopic("topic");

    when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn("test-payload");

    ReflectionTestUtils.invokeMethod(outboxRetryJob, "processRetry", outbox);

    verify(rabbitTemplate).convertAndSend(eq("ex"), eq("rk"), any(Object.class));
    assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
  }
}
