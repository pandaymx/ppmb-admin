package top.ppmblszdp.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ppmb.gateway.rate-limit")
public class GatewayRateLimitProperties {
    /**
     * Whether gateway rate limiting is enabled.
     */
    private boolean enabled = true;

    /**
     * The time period in seconds.
     */
    private int period = 1;

    /**
     * The maximum number of requests allowed in the period.
     */
    private int count = 100;
}
