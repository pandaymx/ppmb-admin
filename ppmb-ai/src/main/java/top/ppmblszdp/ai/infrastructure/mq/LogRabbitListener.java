package top.ppmblszdp.ai.infrastructure.mq;

import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.ppmblszdp.ai.interfaces.web.log.LogSseController;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogRabbitListener {

  private final ChatClient chatClient;
  private final LogSseController logSseController;

  public LogRabbitListener(
      ChatClient.Builder chatClientBuilder, LogSseController logSseController) {
    this.chatClient = chatClientBuilder.build();
    this.logSseController = logSseController;
  }

  @RabbitListener(queues = "q.ppmb.system.log.error")
  public void consumeErrorLog(String logMessage) {
    Executors.newVirtualThreadPerTaskExecutor()
        .submit(
            () -> {
              try {
                String prompt =
                    "Analyze this system error log and provide a root cause analysis and solution: \n"
                        + logMessage;
                String response = chatClient.prompt().user(prompt).call().content();
                logSseController.publishAnalysis(response);
              } catch (Exception e) {
                log.error("Failed to analyze log with AI", e);
              }
            });
  }
}
