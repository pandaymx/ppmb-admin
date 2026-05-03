package top.ppmblszdp.common.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 岗位数据传输对象.
 *
 * @param id 岗位 ID
 * @param postCode 岗位编码
 * @param postName 岗位名称
 * @param sortNum 显示顺序
 * @param status 状态 (0正常 1停用)
 * @param remark 备注
 */
public record PostDto(
    Long id,
    @NotBlank(message = "岗位编码不能为空") @Size(max = 64, message = "岗位编码长度不能超过64") String postCode,
    @NotBlank(message = "岗位名称不能为空") @Size(max = 50, message = "岗位名称长度不能超过50") String postName,
    Integer sortNum,
    Integer status,
    String remark)
    implements Serializable {
  private static final long serialVersionUID = 1L;
}
