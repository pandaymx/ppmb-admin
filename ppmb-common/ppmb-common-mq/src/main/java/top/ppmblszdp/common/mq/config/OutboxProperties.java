package top.ppmblszdp.common.mq.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Outbox 配置属性，支持根据机器资源自动调整线程池参数.
 * 注意：不要添加 @Component，通过 @EnableConfigurationProperties 注册即可.
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "ppmb.mq.outbox")
public class OutboxProperties {

  /** 重试延迟（毫秒），默认 30 秒. */
  private long retryDelay = 30000;

  /** 每批处理的消息数量，默认 20. */
  private int batchSize = 20;

  /** 线程池配置. */
  private Executor executor = new Executor();

  @Data
  public static class Executor {
    /** 核心线程数，默认根据 CPU 核心数计算. */
    private Integer corePoolSize;

    /** 最大线程数，默认根据 CPU 核心数计算. */
    private Integer maxPoolSize;

    /** 队列容量，默认 50. */
    private int queueCapacity = 50;

    /** 线程存活时间（秒），默认 60. */
    private long keepAliveSeconds = 60;

    /** 是否根据机器资源自动配置，默认 true. */
    private boolean autoConfigure = true;
  }

  @PostConstruct
  public void init() {
    if (executor.isAutoConfigure()) {
      autoConfigureExecutor();
    }
    log.info(
        "Outbox executor configured: corePoolSize={}, maxPoolSize={}, queueCapacity={}",
        executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
  }

  private void autoConfigureExecutor() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024; // MB

    // 根据 CPU 核心数和内存自动计算线程池参数
    if (executor.getCorePoolSize() == null) {
      // 核心线程数 = CPU 核心数 / 2，最小为 1，最大为 4
      executor.setCorePoolSize(Math.max(1, Math.min(availableProcessors / 2, 4)));
    }

    if (executor.getMaxPoolSize() == null) {
      // 最大线程数 = CPU 核心数，最小为 2，最大为 8
      executor.setMaxPoolSize(Math.max(2, Math.min(availableProcessors, 8)));
    }

    // 确保 maxPoolSize >= corePoolSize
    if (executor.getMaxPoolSize() < executor.getCorePoolSize()) {
      executor.setMaxPoolSize(executor.getCorePoolSize());
    }

    // 根据内存调整队列容量
    if (maxMemory < 512) {
      // 内存小于 512MB，减少队列容量
      executor.setQueueCapacity(20);
    } else if (maxMemory > 2048) {
      // 内存大于 2GB，可以增加队列容量
      executor.setQueueCapacity(100);
    }

    log.info(
        "Auto-configured outbox executor based on {} CPUs and {}MB memory",
        availableProcessors,
        maxMemory);
  }

  public int getCorePoolSize() {
    return executor.getCorePoolSize();
  }

  public int getMaxPoolSize() {
    return executor.getMaxPoolSize();
  }

  public int getQueueCapacity() {
    return executor.getQueueCapacity();
  }

  public long getKeepAliveSeconds() {
    return executor.getKeepAliveSeconds();
  }
}
