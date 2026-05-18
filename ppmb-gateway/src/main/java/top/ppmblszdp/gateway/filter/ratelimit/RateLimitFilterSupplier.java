package top.ppmblszdp.gateway.filter.ratelimit;

import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilterSupplier extends SimpleFilterSupplier {
    public RateLimitFilterSupplier() {
        super(RateLimitFilterFunctions.class);
    }
}
