package top.ppmblszdp.system.infrastructure.persistence.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.log.entity.SysErrorLog;

@Repository
public interface SysErrorLogRepository extends JpaRepository<SysErrorLog, Long> {}
