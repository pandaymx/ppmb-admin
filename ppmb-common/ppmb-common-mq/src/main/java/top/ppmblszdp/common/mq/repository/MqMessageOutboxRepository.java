package top.ppmblszdp.common.mq.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.common.mq.domain.entity.MqMessageOutbox;
import top.ppmblszdp.common.mq.domain.enums.OutboxStatus;

/**
 * Repository for MqMessageOutbox.
 */
@Repository
public interface MqMessageOutboxRepository extends JpaRepository<MqMessageOutbox, Long> {

    /**
     * Find messages that need to be retried, including those with null nextRetryTime.
     * Use Pageable to limit the result size and avoid OOM.
     */
    @Query("SELECT m FROM MqMessageOutbox m WHERE m.status IN :statuses " +
           "AND (m.nextRetryTime IS NULL OR m.nextRetryTime <= :maxRetryTime) " +
           "AND m.retryCount < :maxRetryCount")
    List<MqMessageOutbox> findPendingOrFailedMessagesForRetry(
            @Param("statuses") List<OutboxStatus> statuses,
            @Param("maxRetryTime") LocalDateTime maxRetryTime,
            @Param("maxRetryCount") Integer maxRetryCount,
            Pageable pageable);
}
