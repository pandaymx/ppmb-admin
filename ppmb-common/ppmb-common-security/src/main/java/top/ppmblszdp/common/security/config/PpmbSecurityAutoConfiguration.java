package top.ppmblszdp.common.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;
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
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
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
  @Order(0)
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ProblemDetailAuthenticationEntryPoint authenticationEntryPoint,
      ProblemDetailAccessDeniedHandler accessDeniedHandler,
      PpmbSecurityProperties properties,
      JwtUtils jwtUtils,
      ObjectMapper objectMapper)
      throws Exception {

    http.securityMatcher(request -> !request.getRequestURI().startsWith("/actuator"))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/", "/index.html", "/favicon.ico", "/error")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/auth/login", "/api/auth/login", "/auth/**", "/api/auth/**")
                    .permitAll()
                    .requestMatchers("/api/*/remote/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

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
