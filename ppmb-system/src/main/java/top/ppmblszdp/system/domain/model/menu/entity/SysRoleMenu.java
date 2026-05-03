package top.ppmblszdp.system.domain.model.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import top.ppmblszdp.common.domain.entity.BaseChildEntity;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_menu")
public class SysRoleMenu extends BaseChildEntity {

  @Column(name = "menu_id", nullable = false)
  private Long menuId;
}
