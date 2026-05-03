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

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_menu")
@SQLRestriction("del_flag = 0")
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
}
