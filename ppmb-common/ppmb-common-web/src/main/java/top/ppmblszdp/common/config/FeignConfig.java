package top.ppmblszdp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Feign 全局配置. */
@Configuration
public class FeignConfig {

  @Bean
  public Encoder feignEncoder() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    return new JacksonEncoder(objectMapper);
  }

  @Bean
  public Decoder feignDecoder() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    return new JacksonDecoder(objectMapper);
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
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
   * <p>注意：非 Servlet 上下文（如启动时、异步线程）调用 Feign 时，RequestContextHolder 返回 null， 此时不做请求头透传。
   *
   * @return RequestInterceptor
   */
  @Bean
  public RequestInterceptor requestInterceptor() {
    return template -> {
      var attributes = RequestContextHolder.getRequestAttributes();
      if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
        // 非 Servlet 上下文（如启动时、异步线程），不做透传
        return;
      }
      HttpServletRequest request = servletAttributes.getRequest();
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
    };
  }
}
