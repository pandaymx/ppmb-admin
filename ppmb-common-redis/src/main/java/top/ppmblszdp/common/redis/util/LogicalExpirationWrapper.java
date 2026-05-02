package top.ppmblszdp.common.redis.util;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LogicalExpirationWrapper<T> {
  private T data;
  private LocalDateTime logicalExpire;
}
