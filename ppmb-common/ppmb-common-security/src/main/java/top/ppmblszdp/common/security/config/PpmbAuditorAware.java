package top.ppmblszdp.common.security.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import top.ppmblszdp.common.security.util.SecurityUtils;

/** JPA Auditing: implementation of {@link AuditorAware} to provide current user ID. */
public class PpmbAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {
    return Optional.ofNullable(SecurityUtils.getUserId());
  }
}
