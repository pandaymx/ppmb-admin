package top.ppmblszdp.common.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import top.ppmblszdp.common.security.data.aspect.DataPermissionAspect;

@AutoConfiguration
public class PpmbDataPermissionAutoConfiguration {

  @Bean
  public DataPermissionAspect dataPermissionAspect() {
    return new DataPermissionAspect();
  }
}
