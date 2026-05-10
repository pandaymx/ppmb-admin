package top.ppmblszdp.common.security.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 数据权限过滤注解. */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

  /**
   * 绑定的菜单权限标识 (例如: sys:user:list). 如果为空，则取用户所有角色的最大数据范围。
   *
   * @return 权限标识
   */
  String permission() default "";

  /**
   * 部门字段别名 (默认 dept_id).
   *
   * @return 部门字段别名
   */
  String deptAlias() default "deptId";

  /**
   * 用户字段别名 (默认 created_by).
   *
   * @return 用户字段别名
   */
  String userAlias() default "createdBy";
}
