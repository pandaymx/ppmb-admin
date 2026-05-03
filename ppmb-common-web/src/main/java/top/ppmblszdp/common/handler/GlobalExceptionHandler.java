package top.ppmblszdp.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.api.log.SysErrorLogMessage;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.common.mq.CommonMessage;
import top.ppmblszdp.common.util.ServletUtils;

/** 全局异常处理器，结合 Java 25 特性和阿里业务码规范. */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(
      Exception ex, HttpServletRequest request, HandlerMethod handlerMethod) {
    // 异步或尽可能不阻塞地记录日志
    sendErrorLog(ex, request, handlerMethod);

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
        yield createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, CommonResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后再试", null);
      }
    };
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

  private void sendErrorLog(Exception ex, HttpServletRequest request, HandlerMethod handlerMethod) {
    try {
      SysErrorLogMessage logMessage = new SysErrorLogMessage();
      logMessage.setRequestUrl(request.getRequestURI());
      logMessage.setHttpMethod(request.getMethod());
      logMessage.setClientIp(ServletUtils.getClientIp(request));
      logMessage.setUserAgent(request.getHeader("User-Agent"));

      // Desensitize parameters
      Map<String, String[]> paramMap = request.getParameterMap();
      if (paramMap != null && !paramMap.isEmpty()) {
        Map<String, String[]> safeParams =
            paramMap.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                          String key = entry.getKey().toLowerCase();
                          if (key.contains("password")
                              || key.contains("token")
                              || key.contains("secret")) {
                            return new String[] {"***"};
                          }
                          return entry.getValue();
                        }));
        logMessage.setRequestParams(objectMapper.writeValueAsString(safeParams));
      }

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
        logMessage.setOperatorAccount(auth.getName());
        // Since custom UserDetails isn't fully set up yet, we'll extract what we can.
        // We'll leave operatorId null if we only have the username string.
      }

      if (handlerMethod != null) {
        logMessage.setClassName(handlerMethod.getBeanType().getName());
        logMessage.setMethodName(handlerMethod.getMethod().getName());
      }

      logMessage.setExceptionType(ex.getClass().getName());
      logMessage.setErrorMessage(ex.getMessage());

      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      String stackTrace = sw.toString();
      if (stackTrace.length() > 4000) {
        stackTrace = stackTrace.substring(0, 4000) + "...";
      }
      logMessage.setStackTrace(stackTrace);

      CommonMessage<SysErrorLogMessage> commonMessage =
          CommonMessage.<SysErrorLogMessage>builder()
              .eventType("SYS_ERROR_LOG")
              .topic("rk.ppmb.sys.log.error")
              .payload(logMessage)
              .build();

      rabbitTemplate.convertAndSend("ex.ppmb.sys.log", "rk.ppmb.sys.log.error", commonMessage);
    } catch (Exception e) {
      log.error("Failed to send error log to MQ", e);
    }
  }
}
