package top.ppmblszdp.system.domain.model.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;

@Entity
@Table(name = "sys_error_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SQLRestriction("del_flag = 0")
public class SysErrorLog extends BaseMainEntity {

  @Column(name = "request_url", length = 512)
  private String requestUrl;

  @Column(name = "http_method", length = 16)
  private String httpMethod;

  @Column(name = "client_ip", length = 128)
  private String clientIp;

  @Column(name = "user_agent", length = 1024)
  private String userAgent;

  @Column(name = "request_params", columnDefinition = "TEXT")
  private String requestParams;

  @Column(name = "operator_id")
  private Long operatorId;

  @Column(name = "operator_account", length = 64)
  private String operatorAccount;

  @Column(name = "class_name", length = 255)
  private String className;

  @Column(name = "method_name", length = 255)
  private String methodName;

  @Column(name = "exception_type", length = 255)
  private String exceptionType;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "stack_trace", columnDefinition = "TEXT")
  private String stackTrace;
}
