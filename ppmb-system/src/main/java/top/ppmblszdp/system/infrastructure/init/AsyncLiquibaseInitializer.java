package top.ppmblszdp.system.infrastructure.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import liquibase.integration.spring.SpringLiquibase;
import javax.sql.DataSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import top.ppmblszdp.common.mq.event.LiquibaseInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Dev 环境下将 Liquibase 初始化延后到应用就绪之后异步执行，避免阻塞启动线程。
 * 迁移完成后再发布完成事件，供后续初始化组件继续执行。
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class AsyncLiquibaseInitializer {

  private final DataSource dataSource;
  private final ResourceLoader resourceLoader;
  private final Environment environment;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @EventListener(ApplicationReadyEvent.class)
  public void initializeLiquibaseAsync() {
    log.info("Starting Liquibase in background after application startup...");

    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setResourceLoader(resourceLoader);
    String changeLog = environment.getProperty("spring.liquibase.change-log", "classpath:db/changelog/db.changelog-master.yaml");
    liquibase.setChangeLog(changeLog);
    // keep defaults for other settings; ensure it runs
    liquibase.setShouldRun(true);

    try {
      liquibase.afterPropertiesSet();
      log.info("Liquibase background initialization finished successfully.");
      eventPublisher.publishEvent(new LiquibaseInitializedEvent());
    } catch (Exception exception) {
      log.error("Liquibase background initialization failed.", exception);
      throw new IllegalStateException("Liquibase background initialization failed", exception);
    }
  }
}