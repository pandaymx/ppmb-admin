package top.ppmblszdp.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.api.constant.MqConstants;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.exception.BusinessException;

/** 全局异常处理器，结合 Java 25 特性和阿里业务码规范. */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private RabbitTemplate rabbitTemplate;

  @org.springframework.beans.factory.annotation.Autowired(required = false)
  public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Value("${spring.application.name:unknown-service}")
  private String serviceName;

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
    return switch (ex) {
      case BusinessException b -> {
        log.warn("业务异常: {}", b.getMessage());
        yield createProblemDetail(b.getStatus(), b.getResultCode(), b.getMessage(), b.getDetail());
      }
      case MethodArgumentNotValidException m -> {
        log.debug("参数校验失败: {}", m.getMessage());
        yield handleValidationException(m);
      }
      case IllegalArgumentException i -> {
        log.debug("非法参数: {}", i.getMessage());
        yield createProblemDetail(
            HttpStatus.BAD_REQUEST, CommonResultCode.PARAM_ERROR, i.getMessage(), null);
      }
      default -> {
        log.error("系统严重异常: ", ex);
        sendExceptionLog(ex, request);
        yield createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, CommonResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后再试", null);
      }
    };
  }

  private void sendExceptionLog(Exception ex, HttpServletRequest request) {
    try {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));

      String userIdStr = request.getHeader("X-User-Id");
      Long userId = (userIdStr != null && !userIdStr.isEmpty()) ? Long.parseLong(userIdStr) : null;

      ExceptionLogMessage message =
          new ExceptionLogMessage(
              serviceName,
              ex.getClass().getName(),
              ex.getMessage(),
              sw.toString(),
              request.getRequestURI(),
              request.getMethod(),
              request.getQueryString(),
              request.getRemoteAddr(),
              userId,
              LocalDateTime.now());

      rabbitTemplate.convertAndSend(
          MqConstants.EXCEPTION_EXCHANGE, MqConstants.EXCEPTION_ROUTING_KEY, message);
    } catch (Exception e) {
      log.error("发送异常日志到 MQ 失败", e);
    }
  }

  private ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    String detail =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining(", "));

    return createProblemDetail(
        HttpStatus.BAD_REQUEST, CommonResultCode.PARAM_ERROR, "参数校验失败", detail);
  }

  private ProblemDetail createProblemDetail(
      HttpStatus status, IResultCode resultCode, String message, String detail) {
    // ProblemDetail 强制要求一个 status，detail 为 null 时使用 message
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(status, detail != null ? detail : message);
    problemDetail.setTitle(resultCode.getMessage());

    // 遵循阿里业务码规范，放入自定义属性
    problemDetail.setProperty("code", resultCode.getCode());
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }
}
