package top.ppmblszdp.common.mq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableScheduling
@EnableJpaRepositories(basePackages = "top.ppmblszdp.common.mq.repository")
@ComponentScan(basePackages = {"top.ppmblszdp.common.mq.service", "top.ppmblszdp.common.mq.job"})
public class ReliableMqConfig {

  @Bean
  public ExecutorService outboxRetryExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
