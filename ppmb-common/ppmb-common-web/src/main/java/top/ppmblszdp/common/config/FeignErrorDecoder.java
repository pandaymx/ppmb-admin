package top.ppmblszdp.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.IResultCode;
import top.ppmblszdp.common.exception.BusinessException;

/** Feign 异常解码器，将远程服务的 ProblemDetail 转换为本地 BusinessException. */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

  /** Jackson 对象映射器. */
  private final ObjectMapper objectMapper;

  /** 默认构造函数，使用默认 ObjectMapper. */
  public FeignErrorDecoder() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * 构造函数.
   *
   * @param objectMapper Jackson 对象映射器
   */
  public FeignErrorDecoder(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    String body = "No body";
    try {
      if (response.body() != null) {
        body = feign.Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        log.warn("Feign 调用异常 [{}], Status: {}, Body: {}", methodKey, response.status(), body);
        BusinessException parsedException = tryParseBusinessException(methodKey, response, body);
        if (parsedException != null) {
          return parsedException;
        }
      }
    } catch (IOException e) {
      log.error("读取 Feign 错误响应失败", e);
    }

    return new BusinessException(
        HttpStatus.valueOf(response.status()),
        CommonResultCode.REMOTE_ERROR,
        "远程服务调用失败",
        "HTTP Status: " + response.status() + ", Body: " + body);
  }

  private BusinessException tryParseBusinessException(
      String methodKey, Response response, String body) {
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = objectMapper.readValue(body, Map.class);

      String title = map.getOrDefault("title", "Remote Service Error").toString();
      String detail = map.getOrDefault("detail", title).toString();
      String code = map.getOrDefault("code", CommonResultCode.REMOTE_ERROR.getCode()).toString();

      IResultCode resultCode =
          new IResultCode() {
            @Override
            public String getCode() {
              return code;
            }

            @Override
            public String getMessage() {
              return title;
            }
          };

      return new BusinessException(
          HttpStatus.valueOf(response.status()), resultCode, title, detail);
    } catch (JsonProcessingException _) {
      log.warn("Feign 错误响应非 JSON 格式 [{}]: {}", methodKey, body);
      return null;
    }
  }
}
