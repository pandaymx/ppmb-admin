package top.ppmblszdp.ai.interfaces.web.log;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.Result;

@RestController
@RequestMapping("/api/ai/log")
@RequiredArgsConstructor
public class LogAnalysisController {

  private final ChatClient chatClient;

  public LogAnalysisController(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  @PostMapping("/analyze")
  @CircuitBreaker(name = "aiService")
  @TimeLimiter(name = "aiService")
  public CompletableFuture<Result<String>> analyzeLog(@RequestBody LogRequest request) {
    return CompletableFuture.supplyAsync(
        () -> {
          String prompt =
              "Please analyze the following system log and provide suggestions for fixing any issues: \n"
                  + request.logText();
          String response = chatClient.prompt().user(prompt).call().content();
          return Result.success(response);
        });
  }

  public record LogRequest(String logText) {}
}
