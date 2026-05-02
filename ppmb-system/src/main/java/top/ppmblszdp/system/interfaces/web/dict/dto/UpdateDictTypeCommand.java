package top.ppmblszdp.system.interfaces.web.dict.dto;

import lombok.Data;

@Data
public class UpdateDictTypeCommand {
  private String dictName;
  private String dictType;
  private Integer status;
  private String remark;
}
