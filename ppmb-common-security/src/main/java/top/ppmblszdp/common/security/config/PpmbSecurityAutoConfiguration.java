package top.ppmblszdp.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.ppmblszdp.common.security.exception.ProblemDetailAccessDeniedHandler;
import top.ppmblszdp.common.security.exception.ProblemDetailAuthenticationEntryPoint;
import top.ppmblszdp.common.security.filter.HeaderAuthenticationFilter;
import top.ppmblszdp.common.security.filter.JwtAuthenticationFilter;
import top.ppmblszdp.common.security.util.JwtUtils;

@AutoConfiguration
@EnableConfigurationProperties(PpmbSecurityProperties.class)
@EnableWebSecurity
public class PpmbSecurityAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public JwtUtils jwtUtils(PpmbSecurityProperties properties) {
    return new JwtUtils(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public ProblemDetailAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
    return new ProblemDetailAuthenticationEntryPoint(objectMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  public ProblemDetailAccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
    return new ProblemDetailAccessDeniedHandler(objectMapper);
  }

  /**
   * Configures the SecurityFilterChain.
   *
   * <p>Note: CSRF protection is disabled because the application uses a stateless architecture with
   * JWT tokens stored in non-cookie headers, which makes it inherently resistant to CSRF attacks
   * that target browser cookie management.
   *
   * @param http the HttpSecurity to configure
   * @param authenticationEntryPoint the authentication entry point
   * @param accessDeniedHandler the access denied handler
   * @param properties the security properties
   * @param jwtUtils the JWT utility
   * @param objectMapper the object mapper
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs
   */
  @Bean
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ProblemDetailAuthenticationEntryPoint authenticationEntryPoint,
      ProblemDetailAccessDeniedHandler accessDeniedHandler,
      PpmbSecurityProperties properties,
      JwtUtils jwtUtils,
      ObjectMapper objectMapper)
      throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            authorize ->
                authorize.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated());

    if (!properties.isGatewayMode()) {
      http.addFilterBefore(
          new HeaderAuthenticationFilter(properties), UsernamePasswordAuthenticationFilter.class);
    } else {
      http.addFilterBefore(
          new JwtAuthenticationFilter(properties, jwtUtils),
          UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }
}
