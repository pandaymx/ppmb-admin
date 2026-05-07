package top.ppmblszdp.common.mq.service;

import top.ppmblszdp.common.mq.CommonMessage;

/**
 * Service for reliable message delivery using the Outbox pattern.
 */
public interface ReliableMessageService {

    /**
     * Send a message reliably. It will be saved to the local database in the current transaction,
     * and published to MQ after the transaction commits.
     *
     * @param exchange The exchange to send to
     * @param routingKey The routing key to use
     * @param message The message payload wrapper
     */
    void send(String exchange, String routingKey, CommonMessage<?> message);
}
