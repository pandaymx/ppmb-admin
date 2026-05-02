package top.ppmblszdp.system.interfaces.web.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDictDataCommand {
  @NotNull(message = "字典类型ID不能为空") private Long parentId;

  @NotNull(message = "字典排序不能为空") private Integer dictSort;

  @NotBlank(message = "字典标签不能为空") private String dictLabel;

  @NotBlank(message = "字典键值不能为空") private String dictValue;

  @NotBlank(message = "字典类型不能为空") private String dictType;

  private String isDefault;
  private String listClass;
  private Integer status;
  private String remark;
}
