package top.ppmblszdp.common.mq.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import top.ppmblszdp.common.mq.CommonMessage;
import top.ppmblszdp.common.mq.domain.entity.MqMessageOutbox;
import top.ppmblszdp.common.mq.repository.MqMessageOutboxRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("可靠消息服务实现测试")
class ReliableMessageServiceImplTest {

  @Mock private MqMessageOutboxRepository outboxRepository;
  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private ReliableMessageServiceImpl reliableMessageService;

  @Test
  @DisplayName("发送消息时应序列化并保存到 Outbox")
  void shouldSaveToOutboxWhenSend() throws JsonProcessingException {
    CommonMessage<String> message =
        CommonMessage.<String>builder()
            .topic("test-topic")
            .eventType("test-event")
            .payload("test-payload")
            .build();

    when(objectMapper.writeValueAsString("test-payload")).thenReturn("{\"data\":\"test\"}");

    try (MockedStatic<TransactionSynchronizationManager> mockedStatic =
        mockStatic(TransactionSynchronizationManager.class)) {
      reliableMessageService.send("test-exchange", "test-routing", message);

      verify(objectMapper).writeValueAsString("test-payload");
      verify(outboxRepository).save(any(MqMessageOutbox.class));
      mockedStatic.verify(() -> TransactionSynchronizationManager.registerSynchronization(any()));
    }
  }
}
