package top.ppmblszdp.common.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.dto.ExceptionLogMessage;
import top.ppmblszdp.common.api.service.ExceptionLogService;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("全局异常处理器测试")
class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;
  private ExceptionLogService exceptionLogService;

  @BeforeEach
  void setUp() {
    exceptionLogService = Mockito.mock(ExceptionLogService.class);
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    handler.setExceptionLogService(exceptionLogService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController()).setControllerAdvice(handler).build();
  }

  @Test
  @DisplayName("处理 BusinessException 时应返回对应的错误码")
  void testHandleBusinessException() throws Exception {
    mockMvc
        .perform(get("/test/business-exception"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(CommonResultCode.USER_ERROR.getCode()))
        .andExpect(jsonPath("$.title").value(CommonResultCode.USER_ERROR.getMessage()));
  }

  @Test
  @DisplayName("处理通用 Exception 时应异步发送日志")
  void testHandleGeneralExceptionAndSendLog() throws Exception {
    mockMvc.perform(get("/test/general-exception")).andExpect(status().isInternalServerError());

    Mockito.verify(exceptionLogService, Mockito.timeout(1000).atLeastOnce())
        .send(Mockito.any(ExceptionLogMessage.class));
  }

  @Test
  @DisplayName("处理通用 Exception 时应返回系统错误码")
  void testHandleGeneralException() throws Exception {
    mockMvc
        .perform(get("/test/general-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(CommonResultCode.SYSTEM_ERROR.getCode()))
        .andExpect(jsonPath("$.detail").value("服务器开小差了，请稍后再试"));
  }

  @Test
  @DisplayName("处理 IllegalArgumentException 时应返回参数错误码")
  void testHandleIllegalArgumentException() throws Exception {
    mockMvc
        .perform(get("/test/illegal-argument"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(CommonResultCode.PARAM_ERROR.getCode()));
  }

  @Test
  @DisplayName("处理参数校验失败时应返回参数错误码及详情")
  void testHandleValidationException() throws Exception {
    mockMvc
        .perform(
            post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(CommonResultCode.PARAM_ERROR.getCode()))
        .andExpect(jsonPath("$.title").value(CommonResultCode.PARAM_ERROR.getMessage()));
  }

  @Test
  @DisplayName("处理通用 Exception 时如果日志服务失败不应影响异常返回")
  void testHandleGeneralExceptionServiceFailure() throws Exception {
    Mockito.doThrow(new RuntimeException("Service Down"))
        .when(exceptionLogService)
        .send(Mockito.any(ExceptionLogMessage.class));

    mockMvc
        .perform(get("/test/general-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(CommonResultCode.SYSTEM_ERROR.getCode()));
  }

  @Test
  @DisplayName("发送日志时应处理各种 X-User-Id 头部情况")
  void testSendExceptionLogWithUserHeaders() throws Exception {
    // Case: Null header
    mockMvc.perform(get("/test/general-exception")).andExpect(status().isInternalServerError());

    // Case: Empty header
    mockMvc
        .perform(get("/test/general-exception").header("X-User-Id", ""))
        .andExpect(status().isInternalServerError());

    // Case: Valid header
    mockMvc
        .perform(get("/test/general-exception").header("X-User-Id", "123"))
        .andExpect(status().isInternalServerError());

    Mockito.verify(exceptionLogService, Mockito.atLeastOnce())
        .send(Mockito.any(ExceptionLogMessage.class));
  }

  @Test
  @DisplayName("测试大堆栈信息的脱敏处理")
  void testSanitizeStackTraceWithLargeStack() throws Exception {
    mockMvc.perform(get("/test/large-stack-exception")).andExpect(status().isInternalServerError());

    Mockito.verify(exceptionLogService)
        .send(
            Mockito.argThat(
                msg -> {
                  String stack = msg.stackTrace();
                  return stack.contains("以及") && stack.contains("个堆栈帧被隐藏");
                }));
  }

  @Test
  @DisplayName("测试空堆栈信息的脱敏处理")
  void testSanitizeStackTraceWithEmptyStack() throws Exception {
    mockMvc.perform(get("/test/empty-stack-exception")).andExpect(status().isInternalServerError());

    Mockito.verify(exceptionLogService)
        .send(
            Mockito.argThat(
                msg -> {
                  String stack = msg.stackTrace();
                  return !stack.contains("[堆栈摘要]");
                }));
  }

  @Test
  @DisplayName("测试默认包（无点号）堆栈信息的处理")
  void testSanitizeStackTraceWithDefaultPackage() throws Exception {
    mockMvc.perform(get("/test/default-package-stack")).andExpect(status().isInternalServerError());

    Mockito.verify(exceptionLogService)
        .send(
            Mockito.argThat(
                msg -> {
                  String stack = msg.stackTrace();
                  return stack.contains("default:");
                }));
  }

  @Test
  @DisplayName("当 ExceptionLogService 为 null 时发送日志不应抛出异常")
  void testSendExceptionLogWithNullService() throws Exception {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    handler.setExceptionLogService(null);
    MockMvc customMockMvc =
        MockMvcBuilders.standaloneSetup(new TestController()).setControllerAdvice(handler).build();

    customMockMvc
        .perform(get("/test/general-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(CommonResultCode.SYSTEM_ERROR.getCode()));
  }

  @RestController
  static class TestController {

    @GetMapping("/test/business-exception")
    void throwBusinessException() {
      throw new BusinessException(CommonResultCode.USER_ERROR);
    }

    @GetMapping("/test/general-exception")
    void throwGeneralException() {
      throw new RuntimeException("Unexpected error");
    }

    @GetMapping("/test/large-stack-exception")
    void throwLargeStackException() {
      RuntimeException ex = new RuntimeException("Large stack");
      StackTraceElement[] elements = new StackTraceElement[30];
      for (int i = 0; i < 30; i++) {
        elements[i] = new StackTraceElement("top.ppmblszdp.Test", "method" + i, "Test.java", i);
      }
      ex.setStackTrace(elements);
      throw ex;
    }

    @GetMapping("/test/empty-stack-exception")
    void throwEmptyStackException() {
      RuntimeException ex = new RuntimeException("Empty stack");
      ex.setStackTrace(new StackTraceElement[0]);
      throw ex;
    }

    @GetMapping("/test/default-package-stack")
    void throwDefaultPackageStack() {
      RuntimeException ex = new RuntimeException("Default package stack");
      StackTraceElement[] elements = new StackTraceElement[1];
      elements[0] = new StackTraceElement("GlobalClass", "method", "GlobalClass.java", 1);
      ex.setStackTrace(elements);
      throw ex;
    }

    @GetMapping("/test/illegal-argument")
    void throwIllegalArgument() {
      throw new IllegalArgumentException("非法参数");
    }

    @PostMapping("/test/validation")
    void validateBody(@Valid @RequestBody TestRequest request) {
      // Intentionally empty. This endpoint is used solely to trigger validation errors.
    }

    record TestRequest(@NotBlank String name) {}
  }
}
