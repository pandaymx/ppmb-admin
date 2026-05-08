package top.ppmblszdp.gateway.filter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

  @Mock private RedisRateLimiter redisRateLimiter;
  @Mock private JwtUtils jwtUtils;

  @InjectMocks private RateLimitFilter rateLimitFilter;

  private MockMvc mockMvc;
  private PpmbSecurityProperties securityProperties;

  @RestController
  static class TestController {
    @GetMapping("/test/rate-limit")
    public String test() {
      return "success";
    }
  }

  @BeforeEach
  void setUp() {
    securityProperties = new PpmbSecurityProperties();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    ReflectionTestUtils.setField(rateLimitFilter, "securityProperties", securityProperties);
    ReflectionTestUtils.setField(rateLimitFilter, "objectMapper", objectMapper);
    ReflectionTestUtils.setField(rateLimitFilter, "rateLimitEnabled", true);
    ReflectionTestUtils.setField(rateLimitFilter, "rateLimitCount", 10);
    ReflectionTestUtils.setField(rateLimitFilter, "rateLimitPeriod", 1);

    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController())
            .addFilter(rateLimitFilter, "/*")
            .build();
  }

  @Test
  @DisplayName("Test IP fallback rate limit allowing request")
  void testIpFallback_Allowed() throws Exception {
    String ip = "192.168.1.100";
    when(redisRateLimiter.isAllowed(eq("rate_limit:ip:" + ip), anyInt(), anyInt()))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Test IP fallback rate limit rejecting request")
  void testIpFallback_Rejected() throws Exception {
    String ip = "192.168.1.100";
    when(redisRateLimiter.isAllowed(eq("rate_limit:ip:" + ip), anyInt(), anyInt()))
        .thenReturn(false);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isTooManyRequests())
        .andExpect(jsonPath("$.code").value(CommonResultCode.USER_ERROR.getCode()));
  }

  @Test
  @DisplayName("Test User Token rate limit allowing request")
  void testUserToken_Allowed() throws Exception {
    String userId = "999";
    String token = "dummy.token.here";
    String headerName = securityProperties.getJwt().getHeaderName();
    String prefix = securityProperties.getJwt().getPrefix();

    when(jwtUtils.extractToken(prefix + token)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(true);

    Claims claims = new DefaultClaims(Map.of("sub", userId));
    when(jwtUtils.parseToken(token)).thenReturn(claims);

    when(redisRateLimiter.isAllowed(eq("rate_limit:user:" + userId), anyInt(), anyInt()))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header(headerName, prefix + token))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Test User Token rate limit rejecting request")
  void testUserToken_Rejected() throws Exception {
    String userId = "999";
    String token = "dummy.token.here";
    String headerName = securityProperties.getJwt().getHeaderName();
    String prefix = securityProperties.getJwt().getPrefix();

    when(jwtUtils.extractToken(prefix + token)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(true);

    Claims claims = new DefaultClaims(Map.of("sub", userId));
    when(jwtUtils.parseToken(token)).thenReturn(claims);

    when(redisRateLimiter.isAllowed(eq("rate_limit:user:" + userId), anyInt(), anyInt()))
        .thenReturn(false);

    mockMvc
        .perform(get("/test/rate-limit").header(headerName, prefix + token))
        .andExpect(status().isTooManyRequests())
        .andExpect(jsonPath("$.code").value(CommonResultCode.USER_ERROR.getCode()));
  }
}
