package top.ppmblszdp.system.domain.model.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/** 异常日志实体. */
@Data
@Entity
@Table(name = "sys_exception_log")
public class SysExceptionLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "exception_name")
  private String exceptionName;

  @Column(name = "message", columnDefinition = "TEXT")
  private String message;

  @Column(name = "stack_trace", columnDefinition = "TEXT")
  private String stackTrace;

  @Column(name = "request_uri")
  private String requestUri;

  @Column(name = "request_method")
  private String requestMethod;

  @Column(name = "request_params", columnDefinition = "TEXT")
  private String requestParams;

  @Column(name = "ip")
  private String ip;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "create_time")
  private LocalDateTime createTime;
}
