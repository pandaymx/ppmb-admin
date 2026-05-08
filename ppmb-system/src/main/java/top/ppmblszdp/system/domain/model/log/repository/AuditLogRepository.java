package top.ppmblszdp.system.domain.model.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.log.entity.SysAuditLog;

/** 审计日志存储库. */
@Repository
public interface AuditLogRepository extends JpaRepository<SysAuditLog, Long> {
  boolean existsByTraceId(String traceId);
}
