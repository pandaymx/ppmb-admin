package top.ppmblszdp.system.interfaces.web.dict.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDictTypeCommand {
  @NotBlank(message = "字典名称不能为空") private String dictName;

  @NotBlank(message = "字典类型不能为空") private String dictType;

  private String systemFlag;
  private Integer status;
  private String remark;
}
