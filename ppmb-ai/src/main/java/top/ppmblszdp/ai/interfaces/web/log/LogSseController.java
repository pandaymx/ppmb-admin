package top.ppmblszdp.ai.interfaces.web.log;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/api/ai/log")
public class LogSseController {

  private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> streamAnalysis() {
    return sink.asFlux();
  }

  public void publishAnalysis(String result) {
    sink.tryEmitNext(result);
  }
}
