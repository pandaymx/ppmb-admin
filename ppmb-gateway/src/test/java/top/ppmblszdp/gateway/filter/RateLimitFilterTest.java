package top.ppmblszdp.gateway.filter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;
import top.ppmblszdp.common.redis.util.RedisRateLimiter;
import top.ppmblszdp.common.redis.util.RedisUtil;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;
import top.ppmblszdp.common.security.util.JwtUtils;
import top.ppmblszdp.gateway.config.RateLimitProperties;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

  @Mock private RedisRateLimiter redisRateLimiter;
  @Mock private JwtUtils jwtUtils;
  @Mock private RedisUtil redisUtil;

  private RateLimitFilter rateLimitFilter;
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
    RateLimitProperties rateLimitProperties = new RateLimitProperties(true, 10, 1, "rate_limit:config");
    ObjectMapper objectMapper = new ObjectMapper();

    rateLimitFilter =
        new RateLimitFilter(
            redisRateLimiter,
            jwtUtils,
            securityProperties,
            rateLimitProperties,
            redisUtil,
            objectMapper);
    when(redisUtil.get(anyString(), eq(String.class))).thenReturn(Optional.empty());

    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController())
            .addFilter(rateLimitFilter, "/*")
            .build();
  }

  @Test
  @DisplayName("IP 降级限流 - 允许请求")
  void testIpFallback_Allowed() throws Exception {
    String ip = "192.168.1.100";
    when(redisRateLimiter.isAllowed(eq("rate_limit:default:ip:" + ip), anyInt(), anyInt()))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isOk());

    verify(redisRateLimiter).isAllowed(eq("rate_limit:default:ip:" + ip), anyInt(), anyInt());
  }

  @Test
  @DisplayName("IP 降级限流 - 拒绝请求并返回 ProblemDetail")
  void testIpFallback_Rejected() throws Exception {
    String ip = "192.168.1.100";
    when(redisRateLimiter.isAllowed(eq("rate_limit:default:ip:" + ip), anyInt(), anyInt()))
        .thenReturn(false);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isTooManyRequests())
        .andExpect(jsonPath("$.title").value("Too Many Requests"))
        .andExpect(jsonPath("$.detail").value("请求过于频繁，请稍后再试"));
  }

  @Test
  @DisplayName("用户 Token 限流 - 允许请求")
  void testUserToken_Allowed() throws Exception {
    String userId = "999";
    String token = "dummy.token.here";
    String headerName = securityProperties.getJwt().getHeaderName();
    String prefix = securityProperties.getJwt().getPrefix();

    when(jwtUtils.extractToken(prefix + token)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(true);

    Claims claims = new DefaultClaims(Map.of("sub", userId));
    when(jwtUtils.parseToken(token)).thenReturn(claims);

    when(redisRateLimiter.isAllowed(eq("rate_limit:default:user:" + userId), anyInt(), anyInt()))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header(headerName, prefix + token))
        .andExpect(status().isOk());

    verify(redisRateLimiter).isAllowed(eq("rate_limit:default:user:" + userId), anyInt(), anyInt());
  }

  @Test
  @DisplayName("用户 Token 限流 - 拒绝请求并返回 ProblemDetail")
  void testUserToken_Rejected() throws Exception {
    String userId = "999";
    String token = "dummy.token.here";
    String headerName = securityProperties.getJwt().getHeaderName();
    String prefix = securityProperties.getJwt().getPrefix();

    when(jwtUtils.extractToken(prefix + token)).thenReturn(Optional.of(token));
    when(jwtUtils.validateToken(token)).thenReturn(true);

    Claims claims = new DefaultClaims(Map.of("sub", userId));
    when(jwtUtils.parseToken(token)).thenReturn(claims);

    when(redisRateLimiter.isAllowed(eq("rate_limit:default:user:" + userId), anyInt(), anyInt()))
        .thenReturn(false);

    mockMvc
        .perform(get("/test/rate-limit").header(headerName, prefix + token))
        .andExpect(status().isTooManyRequests())
        .andExpect(jsonPath("$.title").value("Too Many Requests"))
        .andExpect(jsonPath("$.detail").value("请求过于频繁，请稍后再试"));
  }

  @Test
  @DisplayName("动态规则命中 default 配置")
  void testDynamicRule_DefaultConfig() throws Exception {
    String ip = "192.168.1.100";
    when(redisUtil.get("rate_limit:config:route:default:ip:" + ip, String.class))
        .thenReturn(Optional.empty());
    when(redisUtil.get("rate_limit:config:ip:" + ip, String.class)).thenReturn(Optional.empty());
    when(redisUtil.get("rate_limit:config:route:default", String.class)).thenReturn(Optional.empty());
    when(redisUtil.get("rate_limit:config:default", String.class)).thenReturn(Optional.of("5:2"));
    when(redisRateLimiter.isAllowed(eq("rate_limit:default:ip:" + ip), eq(5), eq(2)))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("动态规则格式非法时回退到本地默认配置")
  void testDynamicRule_InvalidValueFallbackToLocalConfig() throws Exception {
    String ip = "192.168.1.100";
    when(redisUtil.get("rate_limit:config:route:default:ip:" + ip, String.class))
        .thenReturn(Optional.of("bad_value"));
    when(redisUtil.get("rate_limit:config:ip:" + ip, String.class)).thenReturn(Optional.empty());
    when(redisUtil.get("rate_limit:config:route:default", String.class)).thenReturn(Optional.empty());
    when(redisUtil.get("rate_limit:config:default", String.class)).thenReturn(Optional.empty());
    when(redisRateLimiter.isAllowed(eq("rate_limit:default:ip:" + ip), eq(10), eq(1)))
        .thenReturn(true);

    mockMvc
        .perform(get("/test/rate-limit").header("X-Forwarded-For", ip))
        .andExpect(status().isOk());

    verify(redisUtil).get("rate_limit:config:default", String.class);
  }
}
