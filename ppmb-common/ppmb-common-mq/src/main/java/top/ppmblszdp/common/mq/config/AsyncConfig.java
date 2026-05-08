package top.ppmblszdp.common.mq.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

/** 异步处理配置. 配置线程池并支持 SecurityContext 透传. */
@Configuration
@EnableAsync
public class AsyncConfig {

  /** 审计日志专用线程池. 使用 DelegatingSecurityContextAsyncTaskExecutor 以确保 SecurityContext 能透传到异步线程. */
  @Bean(name = "auditAsyncExecutor")
  public Executor auditAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(1000);
    executor.setThreadNamePrefix("audit-async-");
    executor.initialize();
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }
}
