package top.ppmblszdp.common.redis.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoLevelCacheMessage {
  private String cacheName;
  private String key;
}
