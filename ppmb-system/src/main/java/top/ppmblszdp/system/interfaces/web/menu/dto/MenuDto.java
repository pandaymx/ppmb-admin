package top.ppmblszdp.system.interfaces.web.menu.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MenuDto implements Serializable {
  private Long id;
  private String menuName;
  private Long parentId;
  private String menuType;
  private String path;
  private String component;
  private String perms;
  private String icon;
  private Integer orderNum;
  private Boolean visible;
  private LocalDateTime createTime;
  private List<MenuDto> children = new ArrayList<>();
}
