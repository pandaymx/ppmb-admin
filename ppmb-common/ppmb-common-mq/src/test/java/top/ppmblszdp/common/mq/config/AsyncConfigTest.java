package top.ppmblszdp.common.mq.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.concurrent.Executor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@DisplayName("异步配置测试")
class AsyncConfigTest {

  @Test
  @DisplayName("应创建支持 SecurityContext 透传的异步执行器")
  void shouldCreateDelegatingSecurityContextExecutor() {
    AsyncConfig asyncConfig = new AsyncConfig();

    Executor executor = asyncConfig.auditAsyncExecutor();

    assertInstanceOf(DelegatingSecurityContextAsyncTaskExecutor.class, executor);
  }
}
