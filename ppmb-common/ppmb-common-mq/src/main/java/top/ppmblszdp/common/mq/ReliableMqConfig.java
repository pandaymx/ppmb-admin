package top.ppmblszdp.common.mq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import top.ppmblszdp.common.mq.config.OutboxProperties;

@AutoConfiguration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
@ComponentScan(basePackages = {"top.ppmblszdp.common.mq.service", "top.ppmblszdp.common.mq.job"})
public class ReliableMqConfig {

  @Bean
  public ExecutorService outboxRetryExecutor(OutboxProperties outboxProperties) {
    // 使用有界线程池，限制并发数避免数据库连接池耗尽
    // 平台线程比虚拟线程更适合数据库操作，因为可以限制并发

    ThreadFactory threadFactory = r -> {
      Thread t = new Thread(r, "outbox-retry-" + System.currentTimeMillis());
      t.setDaemon(true);
      return t;
    };

    return new ThreadPoolExecutor(
        outboxProperties.getCorePoolSize(),
        outboxProperties.getMaxPoolSize(),
        outboxProperties.getKeepAliveSeconds(),
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(outboxProperties.getQueueCapacity()),
        threadFactory,
        new ThreadPoolExecutor.CallerRunsPolicy());
  }
}
