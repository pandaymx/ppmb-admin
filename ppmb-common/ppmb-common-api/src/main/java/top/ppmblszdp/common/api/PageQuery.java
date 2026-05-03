package top.ppmblszdp.common.api;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.Optional;

/**
 * 分页查询参数.
 *
 * @param pageNum 页码
 * @param pageSize 每页大小
 */
public record PageQuery(
    @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
    @Min(value = 1, message = "每页大小必须大于等于1") Integer pageSize)
    implements Serializable {

  public PageQuery {
    pageNum = Optional.ofNullable(pageNum).orElse(1);
    pageSize = Optional.ofNullable(pageSize).orElse(10);
  }
}
