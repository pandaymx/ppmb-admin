package top.ppmblszdp.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "top.ppmblszdp")
@EnableFeignClients(basePackages = "top.ppmblszdp")
public class AuthApplication {
  public static void main(String[] args) {
    System.setProperty(
        "log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    SpringApplication.run(AuthApplication.class, args);
  }
}
