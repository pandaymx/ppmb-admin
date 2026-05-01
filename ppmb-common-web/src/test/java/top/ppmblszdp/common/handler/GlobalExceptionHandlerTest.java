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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("全局异常处理器测试")
class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
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

  @RestController
  static class TestController {

    @GetMapping("/test/business-exception")
    public void throwBusinessException() {
      throw new BusinessException(CommonResultCode.USER_ERROR);
    }

    @GetMapping("/test/general-exception")
    public void throwGeneralException() {
      throw new RuntimeException("Unexpected error");
    }

    @GetMapping("/test/illegal-argument")
    public void throwIllegalArgument() {
      throw new IllegalArgumentException("非法参数");
    }

    @PostMapping("/test/validation")
    public void validateBody(@Valid @RequestBody TestRequest request) {}

    record TestRequest(@NotBlank String name) {}
  }
}
