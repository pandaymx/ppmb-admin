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

  @Bean
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
