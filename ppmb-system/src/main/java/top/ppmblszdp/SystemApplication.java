package top.ppmblszdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "top.ppmblszdp")
public class SystemApplication {

  public static void main(String[] args) {
    System.setProperty(
        "log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    SpringApplication.run(SystemApplication.class, args);
  }
}
