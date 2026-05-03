package top.ppmblszdp.common.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Feign 全局配置测试")
class FeignConfigTest {

  private final FeignConfig feignConfig = new FeignConfig();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("应正确创建 ErrorDecoder Bean")
  void shouldCreateErrorDecoderBean() {
    ErrorDecoder errorDecoder = feignConfig.errorDecoder(objectMapper);
    assertNotNull(errorDecoder);
  }

  @Test
  @DisplayName("应正确配置 Feign 日志级别")
  void shouldConfigureFeignLoggerLevel() {
    Logger.Level level = feignConfig.feignLoggerLevel();
    assertNotNull(level);
  }

  @Test
  @DisplayName("应正确创建 RequestInterceptor Bean")
  void shouldCreateRequestInterceptorBean() {
    RequestInterceptor interceptor = feignConfig.requestInterceptor();
    assertNotNull(interceptor);
  }
}
