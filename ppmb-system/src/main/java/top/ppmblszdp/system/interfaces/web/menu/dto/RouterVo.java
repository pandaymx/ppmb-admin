package top.ppmblszdp.system.interfaces.web.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo implements Serializable {
  private String name;
  private String path;
  private Boolean hidden;
  private String component;
  private MetaVo meta;
  private List<RouterVo> children;
}
