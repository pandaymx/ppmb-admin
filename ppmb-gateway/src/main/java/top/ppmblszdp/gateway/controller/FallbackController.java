package top.ppmblszdp.gateway.controller;

import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.CommonResultCode;

@RestController
public class FallbackController {

  @RequestMapping("/fallback")
  public ProblemDetail fallback() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "系统当前处于高负载状态或服务不可用，请稍后再试。");
    problemDetail.setType(URI.create("https://api.ppmb.com/errors/service-unavailable"));
    problemDetail.setTitle("Service Unavailable");

    // 遵循阿里业务码规范，放入自定义属性
    problemDetail.setProperty("code", CommonResultCode.SYSTEM_ERROR.getCode());
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }
}
