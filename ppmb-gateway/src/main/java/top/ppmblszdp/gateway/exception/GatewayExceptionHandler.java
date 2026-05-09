package top.ppmblszdp.gateway.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 网关专属异常处理器，确保拦截限流异常等网关层特有异常并返回规范 ProblemDetail. */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GatewayExceptionHandler {

  @ExceptionHandler(RateLimitExceededException.class)
  public ProblemDetail handleRateLimitExceededException(
      RateLimitExceededException ex, HttpServletRequest request) {
    log.warn("触发限流 [{}]: {}", request.getRequestURI(), ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getDetail());
    problemDetail.setType(URI.create("https://api.ppmb.com/errors/too-many-requests"));
    problemDetail.setTitle(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    // 遵循阿里业务码规范，放入自定义属性
    problemDetail.setProperty("code", ex.getResultCode().getCode());
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }
}
