package top.ppmblszdp.common.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果.
 *
 * @param <T> 数据类型
 */
public record PageResult<T>(long total, List<T> list, int pageNum, int pageSize)
    implements Serializable {

  /**
   * 构造分页结果.
   *
   * @param total 总记录数
   * @param list 数据列表
   * @param pageNum 当前页码
   * @param pageSize 每页大小
   * @param <T> 数据类型
   * @return 分页结果
   */
  public static <T> PageResult<T> of(long total, List<T> list, int pageNum, int pageSize) {
    return new PageResult<>(total, list, pageNum, pageSize);
  }

  /**
   * 构造空分页结果.
   *
   * @param pageNum 当前页码
   * @param pageSize 每页大小
   * @param <T> 数据类型
   * @return 空分页结果
   */
  public static <T> PageResult<T> empty(int pageNum, int pageSize) {
    return new PageResult<>(0L, Collections.emptyList(), pageNum, pageSize);
  }
}
