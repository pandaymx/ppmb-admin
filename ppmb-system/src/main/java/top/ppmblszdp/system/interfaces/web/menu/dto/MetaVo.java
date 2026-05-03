package top.ppmblszdp.system.interfaces.web.menu.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaVo implements Serializable {
  private String title;
  private String icon;
  private Boolean noCache;
  private String link;
}
