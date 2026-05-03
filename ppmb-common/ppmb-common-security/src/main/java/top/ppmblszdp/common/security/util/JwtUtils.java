package top.ppmblszdp.common.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import top.ppmblszdp.common.security.config.PpmbSecurityProperties;

/** Utility class for parsing and validating JWTs. */
@Slf4j
public class JwtUtils {

  private final PpmbSecurityProperties properties;
  private final SecretKey key;

  public JwtUtils(PpmbSecurityProperties properties) {
    this.properties = properties;
    if (properties.getJwt().getSecret() != null && !properties.getJwt().getSecret().isEmpty()) {
      byte[] keyBytes = Decoders.BASE64.decode(properties.getJwt().getSecret());
      this.key = Keys.hmacShaKeyFor(keyBytes);
    } else {
      this.key = null;
    }
  }

  /**
   * Extracts token from the Authorization header.
   *
   * @param bearerToken the raw token string
   * @return the token without prefix, or empty if not present or malformed
   */
  public Optional<String> extractToken(String bearerToken) {
    if (bearerToken != null && bearerToken.startsWith(properties.getJwt().getPrefix())) {
      return Optional.of(bearerToken.substring(properties.getJwt().getPrefix().length()).trim());
    }
    return Optional.empty();
  }

  /**
   * Parses the JWT and returns the Claims.
   *
   * @param token the JWT token
   * @return the Claims object
   */
  public Claims parseToken(String token) {
    if (this.key == null) {
      throw new IllegalStateException("JWT Secret is not configured");
    }
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Validates the JWT token.
   *
   * @param token the JWT token
   * @return true if valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Creates a new JWT token.
   *
   * @param subject the subject (e.g. username)
   * @param claims additional claims to include
   * @return the generated JWT token
   */
  public String createToken(String subject, Map<String, Object> claims) {
    if (this.key == null) {
      throw new IllegalStateException("JWT Secret is not configured");
    }

    long now = System.currentTimeMillis();
    Date validity = new Date(now + (properties.getJwt().getExpire() * 1000));

    return Jwts.builder()
        .subject(subject)
        .claims(claims)
        .issuedAt(new Date(now))
        .expiration(validity)
        .signWith(key)
        .compact();
  }
}
