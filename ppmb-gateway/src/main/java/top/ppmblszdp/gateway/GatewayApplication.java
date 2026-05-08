package top.ppmblszdp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务启动类.
 *
 * <p>Gateway 的职责是路由转发，不应该作为 Feign 客户端调用其他服务。 因此不启用 @EnableFeignClients。
 */
@SpringBootApplication
@org.springframework.boot.context.properties.ConfigurationPropertiesScan
public class GatewayApplication {

  public static void main(String[] args) {
    System.setProperty(
        "log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    SpringApplication.run(GatewayApplication.class, args);
  }
}
