package top.ppmblszdp.common.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;
import top.ppmblszdp.common.security.util.JwtUtils;

@DisplayName("安全自动配置测试")
class PpmbSecurityAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(PpmbSecurityAutoConfiguration.class))
          .withUserConfiguration(TestConfig.class);

  @Test
  @DisplayName("应自动注入必要的 Bean")
  void autoConfigurationBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(JwtUtils.class);
          assertThat(context).hasSingleBean(SecurityFilterChain.class);
          assertThat(context).hasSingleBean(PpmbSecurityProperties.class);
        });
  }

  @Test
  @DisplayName("可以自定义配置属性")
  void customProperties() {
    contextRunner
        .withPropertyValues(
            "ppmb.security.gateway-mode=true",
            "ppmb.security.jwt.secret=SGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQ=")
        .run(
            context -> {
              PpmbSecurityProperties props = context.getBean(PpmbSecurityProperties.class);
              assertThat(props.isGatewayMode()).isTrue();
              assertThat(props.getJwt().getSecret())
                  .isEqualTo("SGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQ=");
            });
  }

  @Test
  @DisplayName("非网关模式应注入相应的过滤器")
  void nonGatewayMode() {
    contextRunner
        .withPropertyValues("ppmb.security.gateway-mode=false")
        .run(
            context -> {
              assertThat(context).hasSingleBean(SecurityFilterChain.class);
              PpmbSecurityProperties props = context.getBean(PpmbSecurityProperties.class);
              assertThat(props.isGatewayMode()).isFalse();
            });
  }

  @Configuration
  static class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }
}
