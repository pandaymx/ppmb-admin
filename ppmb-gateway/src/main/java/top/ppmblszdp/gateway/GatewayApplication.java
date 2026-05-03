package top.ppmblszdp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务启动类.
 *
 * <p>Gateway 的职责是路由转发，不应该作为 Feign 客户端调用其他服务。 因此不启用 @EnableFeignClients。
 */
@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }
}
