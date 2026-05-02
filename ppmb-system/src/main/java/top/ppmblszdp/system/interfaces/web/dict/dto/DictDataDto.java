package top.ppmblszdp.system.interfaces.web.dict.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DictDataDto {
  private Long id;
  private Long parentId;
  private Integer dictSort;
  private String dictLabel;
  private String dictValue;
  private String dictType;
  private String isDefault;
  private String listClass;
  private Integer status;
  private String remark;
  private LocalDateTime createTime;
}
