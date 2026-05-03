package top.ppmblszdp.common.api.log;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysErrorLogMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  private String requestUrl;
  private String httpMethod;
  private String clientIp;
  private String userAgent;
  private String requestParams;

  private Long operatorId;
  private String operatorAccount;

  private String className;
  private String methodName;
  private String exceptionType;
  private String errorMessage;
  private String stackTrace;
}
