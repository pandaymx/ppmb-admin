package top.ppmblszdp.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Security configuration properties. */
@Data
@ConfigurationProperties(prefix = "ppmb.security")
public class PpmbSecurityProperties {

  /**
   * Whether this service acts as a gateway (parses JWT directly). If false, the service will rely
   * on headers passed from the gateway.
   */
  private boolean gatewayMode = false;

  /** Configuration for JWT parsing. */
  private Jwt jwt = new Jwt();

  /** Configuration for HTTP Headers used in passthrough mode. */
  private Header header = new Header();

  @Data
  public static class Jwt {
    /** Secret key for parsing JWTs. Must be Base64 encoded. */
    private String secret;

    /** Token prefix, usually "Bearer ". */
    private String prefix = "Bearer ";

    /** The HTTP Header name where the token is expected (e.g. "Authorization"). */
    private String headerName = "Authorization";

    /** Token expiration in seconds. Default is 2 hours. */
    private long expire = 7200;
  }

  @Data
  public static class Header {
    /** Header name for passing the user ID. */
    private String userId = "X-User-ID";

    /** Header name for passing the user name. */
    private String username = "X-User-Name";

    /** Header name for passing user roles. */
    private String roles = "X-User-Roles";
  }
}
