package top.ppmblszdp.common.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 用于标记 Controller 方法以进行业务操作审计日志记录. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
  /**
   * 操作描述.
   *
   * @return 操作描述内容.
   */
  String value() default "";
}
