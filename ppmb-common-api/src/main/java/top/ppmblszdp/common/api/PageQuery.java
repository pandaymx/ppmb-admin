package top.ppmblszdp.common.api;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Data;

/** 分页查询参数. */
@Data
public class PageQuery implements Serializable {

  @Min(value = 1, message = "页码必须大于等于1") private Integer pageNum = 1;

  @Min(value = 1, message = "每页大小必须大于等于1") private Integer pageSize = 10;
}
