package top.ppmblszdp.common.security.data;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ppmblszdp.common.security.data.enums.DataScope;

/** 数据权限上下文数据. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPermissionContext {

  /** 最大权限范围. */
  private DataScope dataScope;

  /** 绑定的部门ID集合 (针对 CUSTOM, DEPT, DEPT_AND_CHILD). */
  private Set<Long> deptIds;

  /** 绑定的用户ID (针对 SELF). */
  private Long userId;

  /** 部门字段别名. */
  private String deptAlias;

  /** 用户字段别名. */
  private String userAlias;
}
