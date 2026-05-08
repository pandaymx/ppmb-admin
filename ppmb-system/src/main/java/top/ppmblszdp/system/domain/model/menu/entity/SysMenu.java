package top.ppmblszdp.system.domain.model.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.tenant.GlobalTable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_menu")
@SQLRestriction("del_flag = 0")
@GlobalTable
public class SysMenu extends BaseMainEntity {

  @Column(name = "menu_name", nullable = false, length = 50)
  private String menuName;

  @Column(name = "parent_id")
  private Long parentId = 0L;

  @Column(name = "menu_type", nullable = false, length = 1)
  private String menuType;

  @Column(name = "path", length = 200)
  private String path;

  @Column(name = "component", length = 255)
  private String component;

  @Column(name = "perms", length = 100)
  private String perms;

  @Column(name = "icon", length = 100)
  private String icon;

  @Column(name = "order_num")
  private Integer orderNum = 0;

  @Column(name = "visible")
  private Boolean visible = true;

  @Transient private List<SysMenu> children = new ArrayList<>();

  /**
   * 获取路由名称.
   *
   * @return 路由名称
   */
  public String getRouteName() {
    if (org.springframework.util.StringUtils.hasText(path)) {
      return org.springframework.util.StringUtils.capitalize(path);
    }
    return "";
  }

  /**
   * 获取组件信息.
   *
   * @return 组件路径或名称
   */
  public String getComponentForRouter() {
    if (org.springframework.util.StringUtils.hasText(component)) {
      return component;
    }
    if (parentId == 0L && "M".equals(menuType)) {
      return "Layout";
    }
    return "ParentView";
  }

  /**
   * 是否为根菜单且类型为目录.
   *
   * @return true 如果是根菜单且为目录
   */
  public boolean isRootDirectory() {
    return parentId == 0L && "M".equals(menuType);
  }

  /**
   * 是否为目录.
   *
   * @return true 如果是目录
   */
  public boolean isDirectory() {
    return "M".equals(menuType);
  }
}
