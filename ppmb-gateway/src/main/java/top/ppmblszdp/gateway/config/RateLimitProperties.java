package top.ppmblszdp.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 网关限流配置属性.
 *
 * @param enabled 是否开启限流
 * @param count 限流阈值
 * @param period 限流周期（秒）
 */
@ConfigurationProperties(prefix = "ppmb.gateway.rate-limit")
public record RateLimitProperties(
    @DefaultValue("true") boolean enabled,
    @DefaultValue("10") int count,
    @DefaultValue("1") int period) {}
