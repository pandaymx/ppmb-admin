package top.ppmblszdp.system.domain.model.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.log.entity.SysExceptionLog;

/** 异常日志存储库. */
@Repository
public interface ExceptionLogRepository extends JpaRepository<SysExceptionLog, Long> {}
