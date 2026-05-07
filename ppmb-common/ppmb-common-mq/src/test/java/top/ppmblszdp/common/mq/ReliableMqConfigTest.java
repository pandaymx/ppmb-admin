package top.ppmblszdp.common.mq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MQ 配置类简单测试")
class ReliableMqConfigTest {

  @Test
  @DisplayName("验证 outboxRetryExecutor 方法返回有效的执行器")
  void testOutboxRetryExecutor() {
    ReliableMqConfig config = new ReliableMqConfig();
    ExecutorService executor = config.outboxRetryExecutor();
    assertThat(executor).isNotNull();
    executor.shutdown();
  }
}
