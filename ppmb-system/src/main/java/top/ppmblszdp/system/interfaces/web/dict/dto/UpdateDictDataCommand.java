package top.ppmblszdp.system.interfaces.web.dict.dto;

import lombok.Data;

@Data
public class UpdateDictDataCommand {
  private Integer dictSort;
  private String dictLabel;
  private String dictValue;
  private String dictType;
  private String isDefault;
  private String listClass;
  private Integer status;
  private String remark;
}
