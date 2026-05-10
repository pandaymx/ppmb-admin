package top.ppmblszdp.common.security.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 数据权限范围枚举. */
@Getter
@AllArgsConstructor
public enum DataScope {

  /** 全部数据权限. */
  ALL(1, "全部数据权限"),

  /** 自定义数据权限 (绑定特定部门). */
  CUSTOM(2, "自定义数据权限"),

  /** 本部门数据权限. */
  DEPT(3, "本部门数据权限"),

  /** 本部门及以下数据权限. */
  DEPT_AND_CHILD(4, "本部门及以下数据权限"),

  /** 仅本人数据权限. */
  SELF(5, "仅本人数据权限");

  private final int value;
  private final String description;

  public static DataScope valueOf(int value) {
    for (DataScope scope : values()) {
      if (scope.getValue() == value) {
        return scope;
      }
    }
    return SELF; // 默认给最小权限
  }
}
