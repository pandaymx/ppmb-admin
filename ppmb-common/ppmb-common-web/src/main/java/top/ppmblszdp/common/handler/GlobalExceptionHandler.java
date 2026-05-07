package top.ppmblszdp.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.api.service.ExceptionLogService;
import top.ppmblszdp.common.exception.BusinessException;

/** 全局异常处理器，结合 Java 25 特性和阿里业务码规范. */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private ExceptionLogService exceptionLogService;

  @org.springframework.beans.factory.annotation.Autowired(required = false)
  public void setExceptionLogService(ExceptionLogService exceptionLogService) {
    this.exceptionLogService = exceptionLogService;
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
      case org.springframework.web.HttpMediaTypeNotSupportedException h -> {
        log.warn("不支持的媒体类型: {}", h.getMessage());
        yield createProblemDetail(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            CommonResultCode.PARAM_ERROR,
            "不支持的媒体类型",
            h.getMessage());
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
    if (exceptionLogService == null) {
      return;
    }
    try {
      String userIdStr = request.getHeader("X-User-Id");
      Long userId = (userIdStr != null && !userIdStr.isEmpty()) ? Long.parseLong(userIdStr) : null;

      ExceptionLogMessage message =
          new ExceptionLogMessage(
              serviceName,
              ex.getClass().getName(),
              ex.getMessage(),
              sanitizeStackTrace(ex),
              request.getRequestURI(),
              request.getMethod(),
              request.getQueryString(),
              request.getRemoteAddr(),
              userId,
              LocalDateTime.now());

      exceptionLogService.send(message);
    } catch (Exception e) {
      log.error("发送异常日志失败", e);
    }
  }

  /** 脱敏处理堆栈信息，避免泄露内部代码结构。 只保留异常类型、消息和首行堆栈，过滤掉项目包名的详细堆栈。. */
  private String sanitizeStackTrace(Exception ex) {
    StringBuilder sb = new StringBuilder();
    sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");

    StackTraceElement[] stackTrace = ex.getStackTrace();
    if (stackTrace.length > 0) {
      // 只保留第一行堆栈（最接近异常发生点）
      sb.append("\tat ").append(stackTrace[0]).append("\n");

      // 添加简短摘要：统计每个包的异常帧数
      sb.append("\n[堆栈摘要]\n");
      java.util.Map<String, Long> packageCounts =
          java.util.Arrays.stream(stackTrace)
              .limit(20) // 限制前20帧
              .collect(
                  Collectors.groupingBy(
                      e -> {
                        String className = e.getClassName();
                        int dotIndex = className.lastIndexOf('.');
                        return dotIndex > 0 ? className.substring(0, dotIndex) : "default";
                      },
                      Collectors.counting()));

      packageCounts.entrySet().stream()
          .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
          .limit(5) // 只显示前5个包
          .forEach(
              e ->
                  sb.append("  ")
                      .append(e.getKey())
                      .append(": ")
                      .append(e.getValue())
                      .append(" 帧\n"));

      if (stackTrace.length > 20) {
        sb.append("  ... 以及 ").append(stackTrace.length - 20).append(" 个堆栈帧被隐藏\n");
      }
    }

    // 记录完整堆栈到日志（供内部排查使用），但不发送到外部系统
    log.debug("完整异常堆栈: ", ex);

    return sb.toString();
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
