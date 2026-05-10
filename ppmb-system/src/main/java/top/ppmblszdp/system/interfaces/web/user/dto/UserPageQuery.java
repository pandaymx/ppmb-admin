package top.ppmblszdp.system.interfaces.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询对象")
public class UserPageQuery {

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "状态 (0:正常, 1:禁用)")
  private Integer status;
}
