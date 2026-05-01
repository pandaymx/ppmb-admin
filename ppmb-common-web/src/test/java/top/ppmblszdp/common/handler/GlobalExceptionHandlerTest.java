package top.ppmblszdp.common.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.exception.BusinessException;

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
  void testHandleBusinessException() throws Exception {
    mockMvc
        .perform(get("/test/business-exception"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(CommonResultCode.USER_ERROR.getCode()))
        .andExpect(jsonPath("$.title").value(CommonResultCode.USER_ERROR.getMessage()));
  }

  @Test
  void testHandleGeneralException() throws Exception {
    mockMvc
        .perform(get("/test/general-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(CommonResultCode.SYSTEM_ERROR.getCode()))
        .andExpect(jsonPath("$.detail").value("服务器开小差了，请稍后再试"));
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
  }
}
