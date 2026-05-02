package top.ppmblszdp.system.interfaces.web.dict.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DictTypeDto {
  private Long id;
  private String dictName;
  private String dictType;
  private String systemFlag;
  private Integer status;
  private String remark;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
