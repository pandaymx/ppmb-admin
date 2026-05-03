package top.ppmblszdp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Feign 全局配置. */
@Configuration
public class FeignConfig {

  /**
   * 注册 Feign 异常解码器.
   *
   * @param objectMapper Jackson 对象映射器
   * @return ErrorDecoder
   */
  @Bean
  public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
    return new FeignErrorDecoder(objectMapper);
  }

  /**
   * 配置 Feign 日志级别.
   *
   * @return Logger.Level
   */
  @Bean
  public Logger.Level feignLoggerLevel() {
    // 生产环境建议使用 BASIC 或 NONE
    return Logger.Level.FULL;
  }

  /**
   * 请求拦截器，实现请求头透传（如 X-User-Id, Authorization 等）.
   *
   * @return RequestInterceptor
   */
  @Bean
  public RequestInterceptor requestInterceptor() {
    return template -> {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
          while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // 透传指定的头信息，或者全部透传（注意安全风险）
            if (name.equalsIgnoreCase("X-User-Id") || name.equalsIgnoreCase("Authorization")) {
              template.header(name, request.getHeader(name));
            }
          }
        }
      }
    };
  }
}
