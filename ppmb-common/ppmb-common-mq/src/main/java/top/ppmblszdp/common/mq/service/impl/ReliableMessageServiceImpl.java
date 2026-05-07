package top.ppmblszdp.common.mq.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import top.ppmblszdp.common.mq.CommonMessage;
import top.ppmblszdp.common.mq.domain.entity.MqMessageOutbox;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;
import top.ppmblszdp.common.mq.repository.MqMessageOutboxRepository;
import top.ppmblszdp.common.mq.service.ReliableMessageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReliableMessageServiceImpl implements ReliableMessageService {

    private final MqMessageOutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void send(String exchange, String routingKey, CommonMessage<?> message) {
        // 1. Serialize message payload
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(message.getPayload());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message payload", e);
            throw new RuntimeException("Message serialization failed", e);
        }

        // 2. Save Outbox record in the current transaction
        MqMessageOutbox outbox = new MqMessageOutbox();
        outbox.setExchange(exchange);
        outbox.setRoutingKey(routingKey);
        outbox.setTopic(message.getTopic());
        outbox.setEventType(message.getEventType());
        outbox.setPayload(payloadJson);
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setRetryCount(0);
        // Ensure nextRetryTime is set initially if your query doesn't handle NULLs.
        // I also updated the query to handle NULLs, but let's initialize it properly as well for the first try.
        outbox.setNextRetryTime(LocalDateTime.now());

        outboxRepository.save(outbox);

        // 3. Register afterCommit hook
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    // Send to MQ
                    rabbitTemplate.convertAndSend(exchange, routingKey, message);

                    // On success, update status to PUBLISHED
                    outbox.setStatus(OutboxStatus.PUBLISHED);
                    outboxRepository.save(outbox);
                    log.debug("Successfully published reliable message for outbox id: {}", outbox.getId());
                } catch (Exception _) {
                    // On failure, do nothing in this transaction (since it's afterCommit, the original business
                    // transaction is already successful). Keep it as PENDING and let the retry job handle it.
                    log.warn("Failed to publish message after commit for outbox id: {}. Will be retried.", outbox.getId());
                }
            }
        });
    }
}
