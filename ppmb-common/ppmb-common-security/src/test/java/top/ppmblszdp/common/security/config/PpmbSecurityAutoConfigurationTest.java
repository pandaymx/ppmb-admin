package top.ppmblszdp.common.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;
import top.ppmblszdp.common.security.exception.ProblemDetailAccessDeniedHandler;
import top.ppmblszdp.common.security.exception.ProblemDetailAuthenticationEntryPoint;
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
          assertThat(context).hasSingleBean(AuditorAware.class);
          assertThat(context).hasSingleBean(ProblemDetailAuthenticationEntryPoint.class);
          assertThat(context).hasSingleBean(ProblemDetailAccessDeniedHandler.class);
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
  @DisplayName("非网关模式应注入 HeaderAuthenticationFilter")
  void nonGatewayMode() {
    contextRunner
        .withPropertyValues("ppmb.security.gateway-mode=false")
        .run(
            context -> {
              assertThat(context).hasSingleBean(SecurityFilterChain.class);
              SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
              // 验证包含 HeaderAuthenticationFilter
              assertThat(
                      filterChain.getFilters().stream()
                          .anyMatch(
                              f ->
                                  f
                                      instanceof
                                      top.ppmblszdp.common.security.filter
                                          .HeaderAuthenticationFilter))
                  .isTrue();

              assertThat(context).hasSingleBean(PasswordEncoder.class);
              assertThat(context).hasSingleBean(ProblemDetailAuthenticationEntryPoint.class);
              assertThat(context).hasSingleBean(ProblemDetailAccessDeniedHandler.class);
            });
  }

  @Test
  @DisplayName("网关模式应注入 JwtAuthenticationFilter")
  void gatewayMode() {
    contextRunner
        .withPropertyValues("ppmb.security.gateway-mode=true")
        .run(
            context -> {
              assertThat(context).hasSingleBean(SecurityFilterChain.class);
              SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
              // 验证包含 JwtAuthenticationFilter
              assertThat(
                      filterChain.getFilters().stream()
                          .anyMatch(
                              f ->
                                  f
                                      instanceof
                                      top.ppmblszdp.common.security.filter.JwtAuthenticationFilter))
                  .isTrue();
            });
  }

  @Test
  @DisplayName("SecurityFilterChain 应排除 /actuator 路径")
  void securityFilterChainActuatorExclusion() {
    contextRunner.run(
        context -> {
          SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
          org.springframework.mock.web.MockHttpServletRequest actuatorRequest =
              new org.springframework.mock.web.MockHttpServletRequest("GET", "/actuator/health");
          assertThat(filterChain.matches(actuatorRequest)).isFalse();

          org.springframework.mock.web.MockHttpServletRequest apiRequest =
              new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/user");
          assertThat(filterChain.matches(apiRequest)).isTrue();
        });
  }

  @Test
  @DisplayName("当用户自定义 Bean 时，自动配置应避让")
  void conditionalOnMissingBean() {
    contextRunner
        .withUserConfiguration(CustomBeanConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(PasswordEncoder.class);
              assertThat(context.getBean(PasswordEncoder.class))
                  .isInstanceOf(TestPasswordEncoder.class);

              assertThat(context).hasSingleBean(AuditorAware.class);
              assertThat(context.getBean(AuditorAware.class))
                  .isNotInstanceOf(PpmbAuditorAware.class);
            });
  }

  @Configuration
  static class CustomBeanConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new TestPasswordEncoder();
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
      return java.util.Optional::empty;
    }
  }

  static class TestPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
      return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return true;
    }
  }

  @Configuration
  static class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }
}
