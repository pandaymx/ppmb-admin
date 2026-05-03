package top.ppmblszdp.common.api.query;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.Optional;

/**
 * 岗位查询参数.
 *
 * @param pageNum 页码
 * @param pageSize 每页大小
 * @param postCode 岗位编码
 * @param postName 岗位名称
 * @param status 状态 (0正常 1停用)
 */
public record PostQuery(
    @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
    @Min(value = 1, message = "每页大小必须大于等于1") Integer pageSize,
    String postCode,
    String postName,
    Integer status)
    implements Serializable {

  public PostQuery {
    pageNum = Optional.ofNullable(pageNum).orElse(1);
    pageSize = Optional.ofNullable(pageSize).orElse(10);
  }
}
