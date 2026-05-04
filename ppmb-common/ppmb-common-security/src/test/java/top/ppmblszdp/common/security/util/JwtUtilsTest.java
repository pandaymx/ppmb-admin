package top.ppmblszdp.common.security.util;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;

@DisplayName("JWT 工具类测试")
class JwtUtilsTest {

  private JwtUtils jwtUtils;
  private PpmbSecurityProperties properties;
  private final String secret =
      "SGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQgSGVsbG8gV29ybGQ="; // 32 bytes

  @BeforeEach
  void setUp() {
    properties = new PpmbSecurityProperties();
    properties.getJwt().setSecret(secret);
    jwtUtils = new JwtUtils(properties);
  }

  @Test
  @DisplayName("从 Authorization Header 提取 Token")
  void extractToken() {
    String header = "Bearer eyJhbGciOiJIUzI1NiJ9...";
    Optional<String> token = jwtUtils.extractToken(header);
    assertTrue(token.isPresent());
    assertEquals("eyJhbGciOiJIUzI1NiJ9...", token.get());

    assertFalse(jwtUtils.extractToken("Invalid token").isPresent());
    assertFalse(jwtUtils.extractToken(null).isPresent());
    assertFalse(jwtUtils.extractToken("Bearer").isPresent());
  }

  @Test
  @DisplayName("构造方法处理空 Secret")
  void constructorWithEmptySecret() {
    PpmbSecurityProperties props1 = new PpmbSecurityProperties();
    props1.getJwt().setSecret("");
    JwtUtils utils1 = new JwtUtils(props1);
    assertThrows(IllegalStateException.class, () -> utils1.parseToken("token"));

    PpmbSecurityProperties props2 = new PpmbSecurityProperties();
    props2.getJwt().setSecret(null);
    JwtUtils utils2 = new JwtUtils(props2);
    assertThrows(IllegalStateException.class, () -> utils2.parseToken("token"));
  }

  @Test
  @DisplayName("解析和验证有效的 Token")
  void parseAndValidateToken() {
    byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    String token =
        Jwts.builder()
            .subject("testuser")
            .expiration(new Date(System.currentTimeMillis() + 10000))
            .signWith(key)
            .compact();

    assertTrue(jwtUtils.validateToken(token));
    Claims claims = jwtUtils.parseToken(token);
    assertEquals("testuser", claims.getSubject());
  }

  @Test
  @DisplayName("验证过期的 Token")
  void validateExpiredToken() {
    byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    String token =
        Jwts.builder()
            .subject("testuser")
            .expiration(new Date(System.currentTimeMillis() - 10000))
            .signWith(key)
            .compact();

    assertFalse(jwtUtils.validateToken(token));
    assertFalse(jwtUtils.validateToken("invalid-token-string"));
    assertFalse(jwtUtils.validateToken(null));
  }

  @Test
  @DisplayName("解析未配置 Secret 的情况应抛出异常")
  void parseTokenWithoutSecret() {
    PpmbSecurityProperties props = new PpmbSecurityProperties();
    JwtUtils utils = new JwtUtils(props);
    assertThrows(IllegalStateException.class, () -> utils.parseToken("some-token"));
    assertThrows(IllegalStateException.class, () -> utils.createToken("subject", Map.of()));
  }

  @Test
  @DisplayName("创建 Token 并验证内容")
  void createAndVerifyToken() {
    String subject = "testuser";
    Map<String, Object> claims = Map.of("role", "admin");

    String token = jwtUtils.createToken(subject, claims);
    assertNotNull(token);

    Claims parsedClaims = jwtUtils.parseToken(token);
    assertEquals(subject, parsedClaims.getSubject());
    assertEquals("admin", parsedClaims.get("role"));
    // 验证有效期
    assertTrue(parsedClaims.getExpiration().after(new Date()));
  }

  @Test
  @DisplayName("验证非法格式的 Token")
  void validateMalformedToken() {
    // 完全不是 JWT 格式的字符串
    assertFalse(jwtUtils.validateToken("not.a.jwt"));
    // 篡改后的 Token (更改签名部分)
    String token = jwtUtils.createToken("user", Map.of());
    String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tampered";
    assertFalse(jwtUtils.validateToken(tamperedToken));
  }
}
